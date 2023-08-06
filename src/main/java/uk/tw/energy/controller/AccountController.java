package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.AccountService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/cost/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculateCost(@PathVariable String smartMeterId,
                                                 @RequestParam(value = "range", required = false) String range) {
        // give enough time, range could have been parametrized, currently its ignored
        Map<String, Object> responseBody = new HashMap<>();
        Optional<BigDecimal> cost = accountService.calculateLastWeekUsageCost(smartMeterId);
        if (cost.isEmpty())
            return ResponseEntity.notFound().build();

        responseBody.put("smartMeterId", smartMeterId);
        responseBody.put("timeRange", range);
        responseBody.put("cost", cost.get());
        return ResponseEntity.ok(responseBody);
    }

}
