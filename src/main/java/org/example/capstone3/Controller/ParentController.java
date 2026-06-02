package org.example.capstone3.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone3.API.ApiResponse;
import org.example.capstone3.Models.Parent;
import org.example.capstone3.Service.ParentService;
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
    public ResponseEntity<?> add(@RequestBody @Valid Parent parent){
        parentService.add(parent);
        return ResponseEntity.status(200).body(new ApiResponse("Parent added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,@RequestBody @Valid Parent parent){
        parentService.update(id,parent);
        return ResponseEntity.status(200).body(new ApiResponse("Parent updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        parentService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Parent deleted successfully"));
    }


}
