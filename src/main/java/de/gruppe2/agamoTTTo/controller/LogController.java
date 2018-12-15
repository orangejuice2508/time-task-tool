package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.base.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("logs")
public class LogController {

    private PoolService poolService;

    private RecordLogService recordLogService;

    @Autowired
    public LogController(PoolService poolService, RecordLogService recordLogService) {
        this.poolService = poolService;
        this.recordLogService = recordLogService;
    }

    /**
     * Method for displaying the "overview" page for the log file.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.VORGESETZTER)
    @GetMapping("/overview")
    public String getOverviewLogsPage(Model model){

        model.addAttribute("pools", getAllPoolsOfAuthenticationUser());
        model.addAttribute("filter", new PoolDateFilter());

        return "logs/overview";
    }

    /**
     * Method for handling the submission of the filter on the "overview" page.
     *
     * @param filter contains criteria set by the user on the overview page
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.VORGESETZTER)
    @PostMapping("overview")
    public String postOverviewLogsPage(@ModelAttribute PoolDateFilter filter, Model model){
        model.addAttribute("filter", filter);
        model.addAttribute("pools", getAllPoolsOfAuthenticationUser());
        model.addAttribute("recordLogs", recordLogService.getAllRecordLogsByFilter(filter));

        return "logs/overview";
    }

    /**
     * Get all pools of the logged in user
     *
     * @return the pools of the logged in user
     */
    private Set<Pool> getAllPoolsOfAuthenticationUser(){
        // Get the logged in user and determine their pools
        return poolService.findAllPoolsOfAUser(SecurityContext.getAuthenticationUser());
    }
}
