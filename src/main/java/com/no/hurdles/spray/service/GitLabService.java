package com.no.hurdles.spray.service;

import com.no.hurdles.spray.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestParams;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 处理gitlab的事件
 * https://docs.gitlab.com/ee/user/project/integrations/webhook_events.html#push-events
 * @author chenwei
 * @version GitLabService, v2.0 2022/9/1 18:32
 **/
@Slf4j
@Service
public class GitLabService {

    /**
     * 处理push回调事件
     * @param event
     */
    public void pushEvent(PushEvent event) throws GitLabApiException {
        GitLabApi gitLabApi = new GitLabApi("https://gitlab.com", "");
        Project projects = gitLabApi.getProjectApi().getProject(39081833);
        System.out.println(GsonUtil.object2String(projects));


        MergeRequestParams params = new MergeRequestParams()
                .withSourceBranch("chenwei777")
                .withTargetBranch("master")
                .withTitle(UUID.randomUUID().toString())
                .withDescription("名称");
        MergeRequest mergeRequest = gitLabApi.getMergeRequestApi()
                .createMergeRequest(39081833, params);
        System.out.println(mergeRequest.getId());
        System.out.println(mergeRequest.getIid());
        //
        //        System.out.println("===");
        MergeRequest acc = gitLabApi.getMergeRequestApi()
                .acceptMergeRequest(39081833, mergeRequest.getIid());
        System.out.println("-----");
    }
}
