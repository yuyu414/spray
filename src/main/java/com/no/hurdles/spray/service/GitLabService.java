package com.no.hurdles.spray.service;

import com.no.hurdles.spray.dao.MergeRulesMapper;
import com.no.hurdles.spray.dao.ProjectInfoMapper;
import com.no.hurdles.spray.model.MergeRules;
import com.no.hurdles.spray.model.ProjectInfo;
import com.no.hurdles.spray.utils.GsonUtil;
import com.no.hurdles.spray.utils.LarkTalkingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestParams;
import org.gitlab4j.api.webhook.EventCommit;
import org.gitlab4j.api.webhook.PushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理gitlab的事件
 * https://docs.gitlab.com/ee/user/project/integrations/webhook_events.html#push-events
 * @author chenwei
 * @version GitLabService, v2.0 2022/9/1 18:32
 **/
@Slf4j
@Service
public class GitLabService {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private MergeRulesMapper mergeRulesMapper;

    @Autowired
    private LarkTalkingUtil larkTalkingUtil;

    /**
     * 处理push回调事件
     * @param event
     */
    public void pushEvent(PushEvent event) throws GitLabApiException {

        Integer projectId = event.getProject().getId();

        ProjectInfo projectInfo = projectInfoMapper.selectByProjectId(projectId.toString());
        if(null == projectInfo){
            throw new RuntimeException("项目不存在或已删除");
        }

        List<EventCommit> commits = event.getCommits();
        String branchName = event.getRef().replace("refs/heads/", "");

        List<MergeRules> list = mergeRulesMapper.selectByPidAndSourceBranch(projectInfo.getId(), branchName);

        for (MergeRules mergeRules : list) {
            GitLabApi gitLabApi = new GitLabApi(projectInfo.getServerUrl(), projectInfo.getAccessToken());

            //查询是否已存在当前分支的合并
            List<MergeRequest> mergeRequestList = gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.OPENED);

            MergeRequest mergeRequest = null;
            for (MergeRequest request : mergeRequestList) {
                if(StringUtils.equals(mergeRules.getSourceBranch(),request.getSourceBranch())
                   && StringUtils.equals(mergeRules.getTargetBranch(),request.getTargetBranch())){
                    mergeRequest = request;
                    break;
                }
            }

            //没有则创建
            if(null == mergeRequest){
                MergeRequestParams params = new MergeRequestParams()
                        .withSourceBranch(mergeRules.getSourceBranch())
                        .withTargetBranch(mergeRules.getTargetBranch())
                        .withTitle("System Merge Request-" + System.currentTimeMillis())
                        .withDescription(GsonUtil.object2String(commits))
                        .withSquash(true);
                mergeRequest = gitLabApi.getMergeRequestApi().createMergeRequest(projectId, params);
            }

            try {
                //发起合并
                gitLabApi.getMergeRequestApi().acceptMergeRequest(projectId, mergeRequest.getIid());
                log.info("合并成功, projectId={}, merge iid={}", projectId, mergeRequest.getIid());
            } catch (Exception e) {
                log.error("合并冲突, projectId={}, merge iid={}", projectId, mergeRequest.getIid(), e);

                List<String> authorList = commits.stream()
                        .map(x -> x.getAuthor().getName())
                        .distinct()
                        .collect(Collectors.toList());

                larkTalkingUtil.sendText("merge冲突: " + mergeRequest.getSourceBranch() + " >>> " + mergeRequest.getTargetBranch() + "\n"//标题
                                         +"Author: " + GsonUtil.object2String(authorList) + "\n"//提交人
                                         +"提交日志: " + GsonUtil.object2String(commits));//日志
            }
        }
    }
}
