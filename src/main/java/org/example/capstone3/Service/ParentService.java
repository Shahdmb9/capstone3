package org.example.capstone3.Service;


import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.DTO.In.ParentDTOIn;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Habit;
import org.example.capstone3.Models.HabitLog;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Repository.ChildRepository;
import org.example.capstone3.Repository.HabitLogRepository;
import org.example.capstone3.Repository.ParentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ModelMapper modelMapper;
    private final CreatePdfService createPdfService;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    private final AiService aiService;
    private final ChildRepository childRepository;
    private final HabitLogRepository habitLogRepository;


        public List<Parent> getAllParents () {
            return parentRepository.findAll();
        }

        public void add (ParentDTOIn parentIn){
            Parent parent = modelMapper.map(parentIn, Parent.class);
            parent.setCreatedAt(java.time.LocalDateTime.now());
            parentRepository.save(parent);
        }

        public void delete (Integer id){
            Parent parent = getParentById(id);
            parentRepository.delete(parent);
        }

        public void update (Integer id, ParentDTOIn parentin){
            Parent parent = modelMapper.map(parentin, Parent.class);
            Parent oldParent = getParentById(id);
            oldParent.setEmail(parent.getEmail());
            oldParent.setFullName(parent.getFullName());
            oldParent.setPassword(parent.getPassword());
            parentRepository.save(oldParent);
        }

        public Parent getParentById (Integer id){
            Parent parent = parentRepository.findParentById(id);
            if (parent == null)
                throw new ApiException("Parent not found");
            return parent;
        }

        public void deductChildPoint(Integer id,Integer childId, Integer points){
            Parent parent=getParentById(id);
            Child child=childRepository.findChildById(childId);
            if(!parent.getChildren().contains(child))
                throw new ApiException("This is not your child");

            if (child==null)
                throw new ApiException("Child not found");

            int updatePoints=child.getPoints()-points;
            if(updatePoints<0)
                child.setPoints(0);
            else child.setPoints(updatePoints);

            childRepository.save(child);
        }

        public void ChildrenPerformanceReport (Integer id, String period){
            Parent parent = parentRepository.findParentById(id);
            if (parent == null)
                throw new ApiException("Parent not found");
            byte[] reportPDF = createPdfService.generatePerformanceReportPdf(parent, period);
            // Send Email
            emailService.sendReportByEmail(parent.getEmail(), reportPDF, parent.getFullName());
            // send WhatsApp
            whatsAppService.sendReportByWhatsApp(parent.getPhoneNumber(), parent.getFullName());

        }

        public byte[] childrenPerformanceReport (Integer id, String period){
            Parent parent = parentRepository.findParentById(id);
            if (parent == null)
                throw new ApiException("Parent not found");
            return createPdfService.generatePerformanceReportPdf(parent, period);

        }


        //  تحليل سلوك الطفل للأب بالذكاء الاصطناعي
        public String getChildBehaviorAnalysis (Integer parentId, Integer childId){
            Parent parent = parentRepository.findParentById(parentId);
            if (parent == null) throw new ApiException("Parent not found");

            Child child = childRepository.findChildById(childId);
            if (child == null) throw new ApiException("Child not found");

            if (!parent.getChildren().contains(child)) throw new ApiException("This is not your child");

            String prompt = "Analyze progress for child: " + child.getFullName() + ", Age: " + child.getAge() + ", Points: " + child.getPoints() + ". Return behavioral analysis in JSON.";

            return aiService.callClaudeApi(prompt);
        }


        //  Pending Habit Approvals (جلب العادات المعلقة لأبناء هذا الأب)
        public List<HabitLog> getPendingHabitApprovals (Integer parentId){
            Parent parent = parentRepository.findParentById(parentId);
            if (parent == null) throw new ApiException("Parent not found");

            List<HabitLog> pendingLogs = new ArrayList<>();

            for (Habit habit : parent.getHabit()) {
                if (habit.getChild() != null) {
                    List<HabitLog> logs = habitLogRepository.findByHabitAndApprovalStatus(habit, "PENDING");
                    pendingLogs.addAll(logs);
                }
            }

            if (pendingLogs.isEmpty()) {
                throw new ApiException("No pending habit logs found for approval");
            }

            return pendingLogs;
        }
    }





