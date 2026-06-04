package org.example.capstone3.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.DTO.In.ParentDTOIn;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Service.ParentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/parent")
public class ParentController {

    private final ParentService parentService;

    @GetMapping("/get")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(parentService.getAllParents());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid ParentDTOIn parent){
        parentService.add(parent);
        return ResponseEntity.status(200).body(new ApiResponse("Parent added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid ParentDTOIn parent){
        parentService.update(id,parent);
        return ResponseEntity.status(200).body(new ApiResponse("Parent updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        parentService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Parent deleted successfully"));
    }
    @GetMapping("{id}/children-report/{period}")
    public ResponseEntity<?> ChildrenPerformanceReport( @PathVariable Integer id , @PathVariable String period){
        parentService.ChildrenPerformanceReport(id, period);
        return ResponseEntity.status(200).body(new ApiResponse("Report sent successfully"));
    }
    @GetMapping("download/{id}/children-report/{period}")
    public ResponseEntity<?> downloadChildrenPerformanceReport(@PathVariable Integer id ,@PathVariable String period){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("Children_Performance_Report", "Report.pdf");
        return ResponseEntity.status(200).headers(headers).body(parentService.childrenPerformanceReport(id, period));
    }

    @PutMapping("/deduct-child-point/{parentId}/{childId}/{poits}")
    public ResponseEntity<?> deductChildPoint(@PathVariable Integer parentId,@PathVariable Integer childId,@PathVariable Integer poits) {
        parentService.deductChildPoint(parentId, childId, poits);
        return ResponseEntity.status(200).body(new ApiResponse("Points deducted successfully"));
    }




}
