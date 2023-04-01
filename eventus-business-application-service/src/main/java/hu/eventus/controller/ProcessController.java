package hu.eventus.controller;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nemeth.mozes@fornax.hu
 */

@RestController
public class ProcessController {

    @Autowired
    private ProcessService processService;
    @Autowired
    private RuntimeDataService runtimeDataService;
    @Autowired
    private UserTaskService userTaskService;

    private DeploymentService deploymentService;

    @PostMapping("/process")
    public ResponseEntity<Long> startProcess(Principal principal){
        Map<String, Object> vars = new HashMap<>();
        vars.put("actor", principal.getName());

        Long processInstanceId = processService.startProcess("eventus-business-application-kjar-1_0-SNAPSHOT",
                "com.mastertheboss.LicenseDemo", vars);
        return ResponseEntity.status(HttpStatus.CREATED).body(processInstanceId);
    }

    @GetMapping("/task")
    public ResponseEntity<List<TaskSummary>> listTasks(Principal principal){
        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner(principal.getName(), new QueryFilter());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskSummaries);
    }

    @GetMapping("/process/{id}")
    public ResponseEntity getProcessById(Principal principal,@PathVariable("id") Long id){
        ProcessInstance processInstance = processService.getProcessInstance(id);
        return ResponseEntity.status(HttpStatus.OK).body(runtimeDataService.getProcessInstanceById(id));
    }
    @GetMapping("/process/{id}/variables")
    public ResponseEntity getProcessVariablesById(Principal principal,@PathVariable("id") Long id){
        Map<String, Object> processInstanceVariables = processService.getProcessInstanceVariables(id);
        return ResponseEntity.status(HttpStatus.OK).body(processInstanceVariables);
    }


    @PostMapping("/task/{taskId}")
    public ResponseEntity executeTask(Principal principal,@PathVariable("taskId") Long taskId,@RequestParam Integer age){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("age", age);
        userTaskService.start(taskId, principal.getName());
        userTaskService.complete(taskId, principal.getName(), params);
        return new ResponseEntity(HttpStatus.OK);
    }
}
