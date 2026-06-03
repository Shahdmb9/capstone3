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

    // 1. الابن يسجل أنه أنهى التحدي الجماعي أولاً (Create Log)
    public void childApplyForTask(Integer childId, Integer taskId) {
        Child child = childRepository.findChildById(childId);
        if (child == null) throw new ApiException("Child not found");

        Task task = taskRepository.findTaskById(taskId);
        if (task == null) throw new ApiException("Task not found");

        if (!task.getStatus().equalsIgnoreCase("PENDING") && !task.getStatus().equalsIgnoreCase("IN_PROGRESS")) {
            throw new ApiException("Task is already closed or completed");
        }

        // منع الطفل من التقديم مرتين على نفس التحدي الجماعي
        List<TaskApplication> existing = taskApplicationRepository.findByTaskIdAndChildId(taskId, childId);
        if (!existing.isEmpty()) {
            throw new ApiException("You have already submitted an application for this task");
        }

        TaskApplication app = new TaskApplication();
        app.setTask(task);
        app.setChild(child);
        app.setApprovalStatus("PENDING"); // معلق بانتظار حسم الأب
        app.setLoggedDate(LocalDate.now());

        taskApplicationRepository.save(app);
    }

    // 2. الأب يوافق على الابن الأسرع، يمنحه الجائزة الكبرى، ويقفل التحدي أمام البقية (Update & Lock)
    public void parentApproveTaskWinner(Integer applicationId, String action) { // action: APPROVED أو REJECTED
        TaskApplication app = taskApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException("Application not found"));

        if (!app.getApprovalStatus().equalsIgnoreCase("PENDING")) {
            throw new ApiException("This application has already been processed");
        }

        Task task = app.getTask();

        if (action.equalsIgnoreCase("APPROVED")) {
            app.setApprovalStatus("APPROVED");
            app.setApprovedAt(LocalDate.now());
            task.setStatus("COMPLETED"); // إغلاق التحدي الجماعي كاملاً لحسم الفائز أولاً

            // تفعيل حالة الجائزة الكبرى وتثبيت تاريخ الاستحقاق الفوري للطفل الفائز
            TaskReward reward = task.getTaskReward();
            if (reward != null) {
                reward.setStatus("COMPLETED");
                reward.setClaimedAt(new java.util.Date());
                taskRewardRepository.save(reward);
            }

            taskRepository.save(task);
        } else {
            app.setApprovalStatus("REJECTED");
        }
        taskApplicationRepository.save(app);
    }
}

