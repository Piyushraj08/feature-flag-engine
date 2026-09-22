package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.teraapp.model.Flag.exception.FlagNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flags")
public class FlagController {

    @Autowired
    private FlagRepository flagRepository;

    @Autowired
    private FlagCacheService flagCacheService;

    @Autowired
    private FlagEventProducer flagEventProducer;

    // GET all flags
    @GetMapping
    public List<Flag> getAllFlags() {
        return flagRepository.findAll();
    }

    // GET one flag by ID
    @GetMapping("/{id}")
    public Flag getFlagById(@PathVariable Long id) {
        return flagRepository.findById(id)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + id));
    }

    // POST create flag
    @PostMapping
    public Flag createFlag(@RequestBody Flag flag) {
        Flag saved = flagRepository.save(flag);

        flagEventProducer.publishEvent(new FlagEvent(
                saved.getId(), saved.getName(), "CREATE", "system", LocalDateTime.now()
        ));   // ← ADD KAR

        return saved;
    }

    // PUT update flag
    @PutMapping("/{id}")
    public Flag updateFlag(@PathVariable Long id, @RequestBody Flag updatedFlag) {
        Flag flag = flagRepository.findById(id)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + id));

        flag.setName(updatedFlag.getName());
        flag.setEnabled(updatedFlag.getEnabled());
        flag.setRolloutPercentage(updatedFlag.getRolloutPercentage());

        Flag saved = flagRepository.save(flag);
        flagCacheService.invalidateCache(id);

        flagEventProducer.publishEvent(new FlagEvent(
                saved.getId(), saved.getName(), "UPDATE", "system", LocalDateTime.now()
        ));   // ← ADD KAR

        return saved;
    }

    // DELETE flag
    @DeleteMapping("/{id}")
    public String deleteFlag(@PathVariable Long id) {
        Flag flag = flagRepository.findById(id)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + id));

        flagRepository.delete(flag);
        flagCacheService.invalidateCache(id);

        flagEventProducer.publishEvent(new FlagEvent(
                flag.getId(), flag.getName(), "DELETE", "system", LocalDateTime.now()
        ));   // ← ADD KAR

        return "Flag deleted successfully with id: " + id;
    }
}