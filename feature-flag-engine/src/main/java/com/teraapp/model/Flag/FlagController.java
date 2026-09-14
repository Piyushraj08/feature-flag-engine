package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.teraapp.model.Flag.exception.FlagNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/flags")
public class FlagController {

    @Autowired
    private FlagRepository flagRepository;

    // GET all flags (already done)
    @GetMapping
    public List<Flag> getAllFlags() {
        return flagRepository.findAll();
    }

    // GET one flag by ID (NEW)
    @GetMapping("/{id}")
    public Flag getFlagById(@PathVariable Long id) {
        return flagRepository.findById(id)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + id));
    }
    // POST create flag (already done)
    @PostMapping
    public Flag createFlag(@RequestBody Flag flag) {
        return flagRepository.save(flag);
    }

    // PUT update flag (NEW)
    @PutMapping("/{id}")
    public Flag updateFlag(@PathVariable Long id, @RequestBody Flag updatedFlag) {
        Flag flag = flagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flag not found with id: " + id));

        flag.setName(updatedFlag.getName());
        flag.setEnabled(updatedFlag.getEnabled());
        flag.setRolloutPercentage(updatedFlag.getRolloutPercentage());

        return flagRepository.save(flag);
    }

    // DELETE flag (NEW)
    @DeleteMapping("/{id}")
    public String deleteFlag(@PathVariable Long id) {
        Flag flag = flagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flag not found with id: " + id));

        flagRepository.delete(flag);
        return "Flag deleted successfully with id: " + id;
    }
}