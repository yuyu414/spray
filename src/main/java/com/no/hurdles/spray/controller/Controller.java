package com.no.hurdles.spray.controller;

import com.no.hurdles.spray.utils.GsonUtil;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestParams;
import org.gitlab4j.api.models.Project;

import java.util.UUID;

/**
 *
 * @author chenwei
 * @version CTRL, v2.0 2022/9/1 18:31
 **/
public class Controller {
    public static void main(String[] args) throws GitLabApiException {

        GitLabApi gitLabApi = new GitLabApi("https://gitlab.com", "glpat-LfRa8fFB9LsFqTNm_Ap_");
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
