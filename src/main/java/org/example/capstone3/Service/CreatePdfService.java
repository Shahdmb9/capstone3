package org.example.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiException;
import org.example.capstone3.Models.Child;
import org.example.capstone3.Models.Parent;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreatePdfService {


    public byte[] generatePerformanceReportPdf(Parent parent, String period) {
        String html = buildHtmlReport(parent, period);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Failed to generate performance report PDF");
        }
    }

    private LocalDateTime getStartDate(String period) {
        return switch (period.toUpperCase()) {
            case "DAY" -> LocalDateTime.now().minusDays(1);
            case "WEEK" -> LocalDateTime.now().minusWeeks(1);
            case "MONTH" -> LocalDateTime.now().minusMonths(1);
            case "YEAR" -> LocalDateTime.now().minusYears(1);
            default -> throw new ApiException("Invalid period. Use DAY, WEEK, MONTH, or YEAR");
        };
    }

    private String buildHtmlReport(Parent parent, String period) {
        LocalDateTime startDate = getStartDate(period);

        StringBuilder childrenRows = new StringBuilder();

        for (Child child : parent.getChildren()) {

            long completedTasks = child.getTask().stream()
                    .flatMap(t -> t.getTaskApplications().stream())
                    .filter(app -> app.getChild().getId().equals(child.getId()))
                    .filter(app -> app.getLoggedDate() != null && app.getLoggedDate().isAfter(startDate.toLocalDate()))
                    .filter(app -> "APPROVED".equalsIgnoreCase(app.getApprovalStatus()))
                    .count();

            long totalTasks = child.getTask().stream()
                    .flatMap(t -> t.getTaskApplications().stream())
                    .filter(app -> app.getChild().getId().equals(child.getId()))
                    .filter(app -> app.getLoggedDate() != null && app.getLoggedDate().isAfter(startDate.toLocalDate()))
                    .count();

            // Filter habits by period

            long completedHabits = child.getHabit().stream()
                    .flatMap(h -> h.getLogs().stream())
                    .filter(log -> log.getLoggedDate() != null && log.getLoggedDate().isAfter(startDate.toLocalDate()))
                    .filter(log -> "COMPLETED".equalsIgnoreCase(log.getApprovalStatus()))
                    .count();

            long totalHabits = child.getHabit().stream()
                    .flatMap(h -> h.getLogs().stream())
                    .filter(log -> log.getLoggedDate() != null && log.getLoggedDate().isAfter(startDate.toLocalDate()))
                    .count();

            int taskPercent = totalTasks > 0 ? (int) ((completedTasks * 100) / totalTasks) : 0;
            int habitPercent = totalHabits > 0 ? (int) ((completedHabits * 100) / totalHabits) : 0;

            childrenRows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%d / %d (%d%%)</td>
                        <td>%d / %d (%d%%)</td>
                    </tr>
                    """.formatted(
                    child.getFullName(),
                    completedTasks, totalTasks, taskPercent,
                    completedHabits, totalHabits, habitPercent
            ));
        }

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 40px; color: #333; }
                        .header { text-align: center; border-bottom: 2px solid #333;
                                  padding-bottom: 20px; margin-bottom: 30px; }
                        .title { font-size: 28px; font-weight: bold; color: #1a1a2e; }
                        .section { margin-bottom: 20px; padding: 15px;
                                   background-color: #f9f9f9; border-radius: 8px; }
                        .label { font-weight: bold; color: #555; }
                        table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
                        th { background-color: #1a1a2e; color: white; padding: 10px; text-align: left; }
                        td { padding: 10px; border-bottom: 1px solid #ddd; }
                        tr:hover { background-color: #f1f1f1; }
                        .footer { margin-top: 50px; text-align: center;
                                  font-size: 12px; color: #999; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <div class="title">Children Performance Report</div>
                        <p>Parent: %s</p>
                        <p>Period: %s &nbsp;|&nbsp; Generated: %s</p>
                    </div>
                
                    <div class="section">
                        <table>
                            <thead>
                                <tr>
                                    <th>Child Name</th>
                                    <th>Tasks Completed</th>
                                    <th>Habits Completed</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s
                            </tbody>
                        </table>
                    </div>
                
                    <div class="footer">
                        <p>Generated automatically by the system</p>
                    </div>
                </body>
                </html>
                """.formatted(
                parent.getFullName(),
                period.toUpperCase(),
                LocalDateTime.now().toString().substring(0, 10),
                childrenRows
        );
    }

    public byte[] generateAiRoadmapPdf(String individualName, String rawJsonRoadmap) {
        StringBuilder phasesHtmlBlocks = new StringBuilder();
        String finalGoal = "Lifestyle Transformation";

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(rawJsonRoadmap);

            if (rootNode.has("finalGoal")) finalGoal = rootNode.get("finalGoal").asText();
            else if (rootNode.has("goal")) finalGoal = rootNode.get("goal").asText();

            if (rootNode.has("phases") && rootNode.get("phases").isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode phase : rootNode.get("phases")) {
                    String phaseName = phase.has("phaseName") ? phase.get("phaseName").asText() : "Next Stage";
                    String focus = phase.has("focus") ? phase.get("focus").asText() : "General Progress";

                    StringBuilder habitsRows = new StringBuilder();
                    if (phase.has("recommendedHabits") && phase.get("recommendedHabits").isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode habit : phase.get("recommendedHabits")) {
                            String title = habit.has("title") ? habit.get("title").asText() : "New Habit";
                            String desc = habit.has("description") ? habit.get("description").asText() : "";
                            String points = habit.has("expectedPoints") ? habit.get("expectedPoints").asText() : "10";

                            habitsRows.append("<tr>")
                                    .append("<td style=\"font-weight:bold;\">").append(title).append("</td>")
                                    .append("<td>").append(desc).append("</td>")
                                    .append("<td style=\"text-align:center;\">").append(points).append("</td>")
                                    .append("</tr>");
                        }
                    }

                    phasesHtmlBlocks.append("<div class=\"section\">")
                            .append("<div class=\"label\" style=\"font-size: 16px; color: #1a1a2e; margin-bottom: 5px;\">Phase: ").append(phaseName).append("</div>")
                            .append("<div style=\"font-size: 12px; color: #666; margin-bottom: 10px;\">Focus: ").append(focus).append("</div>")
                            .append("<table>")
                            .append("<thead><tr><th>Habit Title</th><th>Description</th><th>Points</th></tr></thead>")
                            .append("<tbody>").append(habitsRows.toString()).append("</tbody>")
                            .append("</table>")
                            .append("</div>");
                }
            }
        } catch (Exception e) {
            String safeText = rawJsonRoadmap.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
            phasesHtmlBlocks.append("<div class=\"section\"><div class=\"label\">Roadmap Text Details:</div><p style=\"font-size:13px; line-height:1.5;\">")
                    .append(safeText).append("</p></div>");
        }

        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; padding: 40px; color: #333; }" +
                "        .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 30px; }" +
                "        .title { font-size: 28px; font-weight: bold; color: #1a1a2e; }" +
                "        .section { margin-bottom: 25px; padding: 15px; background-color: #f9f9f9; border-radius: 8px; }" +
                "        .label { font-weight: bold; color: #555; }" +
                "        table { width: 100%; border-collapse: collapse; margin-top: 15px; }" +
                "        th { background-color: #1a1a2e; color: white; padding: 8px; text-align: left; font-size: 12px; }(" +
                "        td { padding: 8px; border-bottom: 1px solid #ddd; font-size: 12px; }" +
                "        tr:hover { background-color: #f1f1f1; }" +
                "        .footer { margin-top: 50px; text-align: center; font-size: 12px; color: #999; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"header\">" +
                "        <div class=\"title\">Smart Habit Roadmap</div>" +
                "        <p>Individual: " + individualName + "</p>" +
                "        <p>Final Goal: " + finalGoal + " &nbsp;|&nbsp; Generated: " + java.time.LocalDate.now() + "</p>" +
                "    </div>" +
                "    " + phasesHtmlBlocks.toString() + "" +
                "    <div class=\"footer\">" +
                "        <p>Generated automatically by AI System Companion</p>" +
                "    </div>" +
                "</body>" +
                "</html>";

        try {
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            org.xhtmlrenderer.pdf.ITextRenderer renderer = new org.xhtmlrenderer.pdf.ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new org.example.capstone3.API.ApiException("Failed to generate AI roadmap PDF");
        }
    }

}
