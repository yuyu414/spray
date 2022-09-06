package com.no.hurdles.spray.controller;

import com.no.hurdles.spray.utils.GitUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author chenwei
 * @version CTRL, v2.0 2022/9/1 18:31
 **/
public class Controller {

    public static void main(String[] args) throws IOException, GitAPIException {

        //GitUtil.repositoryInit("https://gitlab.com/1041579785/demo.git", "D:/spraywork" + File.separator + "demo", "1041579785@qq.com", "qwertyuiop");

        Git git = GitUtil.buildGit("D:/spraywork" + File.separator + "demo");

        if(!GitUtil.isBranch(git, "chenwei777")){
            throw new RuntimeException("分支不存在");
        }


        GitUtil.checkout(git, "chenwei777");
        System.out.println("================" + git.getRepository().getBranch());
        GitUtil.pull(git, "chenwei777", "1041579785@qq.com", "qwertyuiop");

        System.out.println("================" + git.getRepository().getBranch());
        GitUtil.checkout(git, "master");
        System.out.println("================" + git.getRepository().getBranch());
        GitUtil.pull(git, "master", "1041579785@qq.com", "qwertyuiop");
        System.out.println("================" + git.getRepository().getBranch());


        MergeResult result = GitUtil.tryMerge(git, "chenwei777");
        if (!result.getMergeStatus().isSuccessful()) {
            //发送提醒合并失败
            System.out.println(result.getMergeStatus());
            return;
        }
        System.out.println("================" + git.getRepository().getBranch());
        GitUtil.push(git,"1041579785@qq.com", "qwertyuiop");
        System.out.println("================" + git.getRepository().getBranch());
    }
}
