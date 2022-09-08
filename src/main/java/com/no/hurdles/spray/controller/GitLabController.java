package com.no.hurdles.spray.controller;

import com.no.hurdles.spray.service.GitLabService;
import com.no.hurdles.spray.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("gitlab")
public class GitLabController {
    
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private GitLabService gitLabService;

    /**
     * gitlab仓库push操作时的回调接口
     * @param event
     * @return
     */
    @PostMapping("event/push")
    public String pushEvent(@RequestBody PushEvent event) throws GitLabApiException {
        String pushHook = request.getHeader("X-Gitlab-Event");
        log.info("GitLab 推送事件, event={}, param={}", pushHook, GsonUtil.object2String(event));
        if(!StringUtils.equals("Push Hook", pushHook)){
            //非推送事件直接返回
            return pushHook;
        }
        gitLabService.pushEvent(event);
        return "success";
    }
}