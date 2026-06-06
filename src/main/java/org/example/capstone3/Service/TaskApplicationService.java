package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.*;
import org.example.capstone3.Repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskApplicationService {

    private final TaskApplicationRepository taskApplicationRepository;
    private final TaskRepository taskRepository;
    private final ChildRepository childRepository;
    private final TaskRewardRepository taskRewardRepository;
    private final WhatsAppService whatsAppService;

    public void childApplyForTask(Integer childId, Integer taskId) {
        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");

        Task task = taskRepository.findTaskById(taskId);
        if (task == null) throw new ApiException("Task not found");

        if (!task.getStatus().equalsIgnoreCase("PENDING") && !task.getStatus().equalsIgnoreCase("IN_PROGRESS")) {
            throw new ApiException("Task is already closed or completed");
        }

        List<TaskApplication> existing = taskApplicationRepository.findByTaskIdAndChildId(taskId, childId);
        if (!existing.isEmpty()) {
            throw new ApiException("You have already submitted an application for this task");
        }

        TaskApplication app = new TaskApplication();
        app.setTask(task);
        app.setChild(child);
        app.setApprovalStatus("PENDING");
        app.setLoggedDate(LocalDate.now());

        taskApplicationRepository.save(app);

        if (task.getParent() != null) {
            Parent parent = task.getParent();
            whatsAppService.sendReportByWhatsApp(parent.getPhoneNumber(), "تنبيه: قام الابن " + child.getFullName() + " بتقديم طلب إنجاز للمهمة الجماعية: " + task.getTitle());
        }
    }

    public List<TaskApplication> getPendingApplicationsForParent(Integer parentId) {
        List<TaskApplication> allPending = taskApplicationRepository.findAll();
        List<TaskApplication> parentPending = new java.util.ArrayList<>();

        for (TaskApplication app : allPending) {
            if ("PENDING".equalsIgnoreCase(app.getApprovalStatus()) && app.getTask() != null && app.getTask().getParent() != null) {
                if (app.getTask().getParent().getId().equals(parentId)) {
                    parentPending.add(app);
                }
            }
        }
        return parentPending;
    }


    public void parentApproveTaskWinner(Integer applicationId, String action) {
        TaskApplication app = taskApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException("Application not found"));

        if (!app.getApprovalStatus().equalsIgnoreCase("PENDING")) {
            throw new ApiException("This application has already been processed");
        }

        Task task = app.getTask();

        if (action.equalsIgnoreCase("APPROVED")) {
            app.setApprovalStatus("APPROVED");
            app.setApprovedAt(LocalDate.now());

            task.setStatus("COMPLETED");

            TaskReward reward = task.getTaskReward();
            if (reward != null) {
                reward.setStatus("COMPLETED");
                reward.setClaimedAt(new java.util.Date());

                reward.setWinnerChild(app.getChild());

                taskRewardRepository.save(reward);
            }

            taskRepository.save(task);

        } else {
            app.setApprovalStatus("REJECTED");
        }
        taskApplicationRepository.save(app);
    }

}

