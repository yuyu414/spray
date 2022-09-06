package com.no.hurdles.spray.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;

import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * git操作工具
 * 更多api参考：https://github.com/centic9/jgit-cookbook
 */
@Slf4j
public class GitUtil {

    //定义本地git工作目录
    public static final String BaseDir = "D:/spraywork" + File.separator;
    public static final String RepositoryName = "demo";
    public static final String githubUrl = "https://xxx.com/demo.git";
    public static final String USER = "USER";
    public static final String PASSWORD = "PASSWORD";

    public static void main(String[] args) {
//        repositoryInit(githubUrl, BaseDir + RepositoryName, USER, PASSWORD);
//
//        Git git = buildGit(BaseDir + RepositoryName);
//        System.out.println(getLocalBranchList(git));
//        System.out.println(getOriginBranchList(git));
    }



    private static final String LOCAL_BRANCH_REF_PREFIX = "refs/remotes/origin/";
    private static final int TIMEOUT = 20;

    /**
     * 初始化仓库，只需要执行一次
     * @param repositoryUrl  仓库地址
     * @param localPath 本地初始化路径
     * @param username git账号
     * @param password git密码
     * @return
     */
    public static Git repositoryInit(String repositoryUrl, String localPath, String username, String password) {
        try {
            //清除旧文件
            File file = new File(localPath);
            if (file.exists()){
                deleteFile(file);
            }
            return Git.cloneRepository()
               .setURI(repositoryUrl)
               .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
               .setCloneAllBranches(true)
               .setDirectory(file)
               .setTimeout(TIMEOUT)
               .call();
        } catch (Exception e) {
            log.error("repositoryInit fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 构建git对象
     * @return
     */
    public static Git buildGit(String localPath) {
        try {
            return new Git(new FileRepository(localPath + File.separator + ".git"));
        } catch (IOException e) {
            log.error("buildGit fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 把当前分支提交
     * @param git
     * @param commitMessage
     * @throws IOException
     * @throws GitAPIException
     */
    public static RevCommit commit(Git git, String commitMessage) {
        try {
            return git.commit().setAll(true).setMessage(commitMessage).call();
        } catch (GitAPIException e) {
            log.error("commit fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 推送到远端
     * @param git
     * @throws IOException
     * @throws GitAPIException
     */
    public static Iterable<PushResult> push(Git git, String username, String password) {
        try {
            return git.push()
                    .setForce(true)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .setTimeout(TIMEOUT)
                    .call();
        } catch (GitAPIException e) {
            log.error("push fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取远端分支列表
     * @return
     */
    public static List<Ref> getOriginBranchList(Git git) {
        try {
            return git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        } catch (GitAPIException e) {
            log.error("getOriginBranchList fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取本地分支列表
     * @return
     */
    public static List<Ref> getLocalBranchList(Git git) {
        try {
            return git.branchList().call();
        } catch (GitAPIException e) {
            log.error("getLocalBranchList fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 创建远端分支
     * @param branchName
     * @throws IOException
     * @throws GitAPIException
     */
    public static void createOriginBranch(Git git, String branchName, String username, String password) {

        try {
            //创建本地分支
            git.branchCreate().setName(branchName).setForce(true).call();
            //推送远端分支
            git.push()
                    .setRefSpecs(new RefSpec(branchName + ":" + branchName))// local:origin
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .call();
        } catch (GitAPIException e) {
            log.error("createOriginBranch fail", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 创建本地分支
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static Ref createLocalBranch(Git git, String branchName) {
        try {
            return git.branchCreate().setName(branchName).setForce(true).call();
        } catch (GitAPIException e) {
            log.error("createLocalBranch fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 删除远程分支
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static Iterable<PushResult> deleteOriginBranch(Git git, String branchName, String username, String password){
        try {
            return git.push()
                    .setRefSpecs(new RefSpec(":refs/heads/" + branchName))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .call();
        } catch (GitAPIException e) {
            log.error("deleteOriginBranch fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 删除本地分支
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static List<String> deleteLocalBranch(Git git, String branchName) {
        try {
            return git.branchDelete().setBranchNames(branchName).setForce(true).call();
        } catch (GitAPIException e) {
            log.error("deleteLocalBranch fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 判断本地分支名是否存在
     * @param git
     * @param branchName
     * @return
     */
    public static boolean localBranchIsExist(Git git, String branchName) {
        List<Ref> refs = getLocalBranchList(git);
        for (Ref ref : refs) {
            if (ref.getName().contains(branchName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check分支到本地
     * @param git
     * @param branchName
     * @return
     */
    public static Ref checkout(Git git, String branchName) {

        try {
            CheckoutCommand checkout = git.checkout();
            if (shouldTrack(git, branchName)) {
                checkout.setCreateBranch(true)
                        .setName(branchName)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                        .setStartPoint("origin/" + branchName);
            } else {
                checkout.setName(branchName);
            }
            return checkout.call();
        } catch (GitAPIException e) {
            log.error("checkout fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * reset与远端分支保持一致
     * @param git
     * @param remoteBranchName
     * @return
     */
    public static Ref resetHard(Git git, String remoteBranchName) {
        try {
            ResetCommand reset = git.reset();
            reset.setRef(remoteBranchName);
            reset.setMode(ResetCommand.ResetType.HARD);
            Ref resetRef = reset.call();
            if (resetRef != null) {
                log.info("RESET 本地分支={} 为远端分支={} version={}", git.getRepository().getBranch(), GsonUtil.object2String(resetRef.getObjectId()));
            }
            return resetRef;
        } catch (Exception e) {
            log.error("resetHard fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 合并分支
     * @param branchName
     */
    public static MergeResult tryMerge(Git git, String branchName) {
        try {
            MergeResult result = merge(git, branchName);
            log.info("tryMerge result={}", GsonUtil.object2String(result));
            if (!isClean(git, branchName)) {
                log.warn("当前分支是脏分支，执行reset操作");
                resetHard(git, LOCAL_BRANCH_REF_PREFIX + git.getRepository().getBranch());
            }
            return result;
        } catch (Exception e) {
            log.error("tryMerge fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * fench操作
     * @param git
     * @param branchName
     * @param username
     * @param password
     * @return
     */
    public static FetchResult fetch(Git git, String branchName, String username, String password) {
        try {
            FetchCommand fetch = git.fetch();
            fetch.setRemote("origin");
            fetch.setTagOpt(TagOpt.FETCH_TAGS);
            fetch.setRemoveDeletedRefs(true);
            fetch.setTimeout(TIMEOUT);
            fetch.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
            FetchResult result = fetch.call();
            if (result.getTrackingRefUpdates() != null && result.getTrackingRefUpdates().size() > 0) {
                log.info("Fetched for remote " + branchName + " and found " + result.getTrackingRefUpdates().size() + " updates");
            }
            return result;
        } catch (Exception e) {
            log.error("fetch fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * fench操作
     * @param git
     * @param branchName
     * @param username
     * @param password
     * @return
     */
    public static PullResult pull(Git git, String branchName, String username, String password) {
        try {
            return git.pull()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .setRemote("origin")
                    .setRemoteBranchName(branchName)
                    .setTimeout(GitUtil.TIMEOUT)
                    .call();
        } catch (Exception e) {
            log.error("pull fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 如果删除了相应的远程分支，则删除本地分支
     * @param git
     * @param defaultBranchName 默认分支
     * @param trackingRefUpdates
     * @return
     */
    private static Collection<String> deleteUntrackedLocalBranches(Git git, String defaultBranchName, Collection<TrackingRefUpdate> trackingRefUpdates) {
        if (CollectionUtils.isEmpty(trackingRefUpdates)) {
            return Collections.emptyList();
        }

        Collection<String> branchesToDelete = new ArrayList<>();
        for (TrackingRefUpdate trackingRefUpdate : trackingRefUpdates) {
            ReceiveCommand receiveCommand = trackingRefUpdate.asReceiveCommand();
            if (receiveCommand.getType() == ReceiveCommand.Type.DELETE) {
                String localRefName = trackingRefUpdate.getLocalName();
                if (StringUtils.startsWithIgnoreCase(localRefName, LOCAL_BRANCH_REF_PREFIX)) {
                    String localBranchName = localRefName.substring(LOCAL_BRANCH_REF_PREFIX.length());
                    branchesToDelete.add(localBranchName);
                }
            }
        }

        if (CollectionUtils.isEmpty(branchesToDelete)) {
            return Collections.emptyList();
        }
        try {
            //确保删除的分支不是当前分支
            checkout(git, defaultBranchName);
            return deleteBranches(git, branchesToDelete);
        } catch (GitAPIException e) {
            log.error("deleteUntrackedLocalBranches Failed to delete branches " + GsonUtil.object2String(branchesToDelete), e);
            return Collections.emptyList();
        }
    }

    private static List<String> deleteBranches(Git git, Collection<String> branchesToDelete) throws GitAPIException {
        DeleteBranchCommand deleteBranchCommand = git.branchDelete()
                .setBranchNames(branchesToDelete.toArray(new String[0]))
                // local branch can contain data which is not merged to HEAD - force
                // delete it anyway, since local copy should be R/O
                .setForce(true);
        List<String> resultList = deleteBranchCommand.call();
        return resultList;
    }

    /**
     * 判断是否为分支
     * @param git
     * @param branchName
     * @return
     */
    public static boolean isBranch(Git git, String branchName) {
        return containsBranch(git, branchName, ListBranchCommand.ListMode.ALL);
    }

    /**
     * 删除文件或者目录
     *
     * @param sourceFile 原文件 / 目录
     * @date 2022/6/28 20:37
     **/
    private static void deleteFile(File sourceFile) {
        // 判断参数
        if (sourceFile == null) {
            return;
        }
        // 判断是否是目录
        if (sourceFile.isDirectory()) {
            File[] childrenFile = sourceFile.listFiles();
            if (childrenFile != null && childrenFile.length > 0) {
                for (File childFile : childrenFile) {
                    // 删除子级 文件 / 目录
                    deleteFile(childFile);
                }
            }
        }
        // 删除 文件 / 目录 本身，不要用deleteOnExit()方法，不然无法删除目录
        sourceFile.delete();
    }

    private static String getConventionalCommitMessage(RevCommit commit) {
        StringBuilder stringBuilder = new StringBuilder();

        // Prepare the pieces
        final String justTheAuthorNoTime = commit.getAuthorIdent().toExternalString().split(">")[0] + ">";
        final Instant commitInstant = Instant.ofEpochSecond(commit.getCommitTime());
        final ZoneId zoneId = commit.getAuthorIdent().getTimeZone().toZoneId();
        final ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, zoneId);
        final String gitDateTimeFormatString = "EEE MMM dd HH:mm:ss yyyy Z";
        final String formattedDate = authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));
        final String tabbedCommitMessage = Arrays.stream(commit.getFullMessage()
                        .split("\\r?\\n")) // split it up by line
                        .map(s -> "\t" + s + "\n") // add a tab on each line
                        .collect(Collectors.joining()); // put it back together

        // Put pieces together
        stringBuilder
                .append("commit ").append(commit.getName()).append("\n")
                .append("Author:\t").append(justTheAuthorNoTime).append("\n")
                .append("Date:\t").append(formattedDate).append("\n\n")
                .append(tabbedCommitMessage);

        return stringBuilder.toString();
    }

    /**
     * 是否包含分支
     * @param git
     * @param branchName
     * @param listMode
     * @return
     * @throws GitAPIException
     */
    private static boolean containsBranch(Git git, String branchName, ListBranchCommand.ListMode listMode) {

        try {
            ListBranchCommand command = git.branchList();
            if (listMode != null) {
                command.setListMode(listMode);
            }
            List<Ref> branches = command.call();
            for (Ref ref : branches) {
                if (ref.getName().endsWith("/" + branchName)) {
                    return true;
                }
            }
            return false;
        } catch (GitAPIException e) {
            log.error("containsBranch fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private static boolean shouldTrack(Git git, String branchName) {
        return isBranch(git, branchName) && !isLocalBranch(git, branchName);
    }

    private static boolean isLocalBranch(Git git, String branchName) {
        return containsBranch(git, branchName, null);
    }

    private static MergeResult merge(Git git, String branchName) {
        try {
            MergeCommand merge = git.merge();
            merge.include(git.getRepository().findRef("origin/" + branchName));
            merge.setCommit(true);
            merge.setMessage("system merge: " + branchName + ">>>" + git.getRepository().getBranch());
            MergeResult result = merge.call();
            if (!result.getMergeStatus().isSuccessful()) {
                log.warn(git.getRepository().getBranch() + " 合并 ---> " + branchName + " 失败, status=" + result.getMergeStatus());
            }
            return result;
        } catch (Exception e) {
            log.error("merge fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private static boolean isClean(Git git, String branchName) {
        try {
            StatusCommand status = git.status();
            BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(git.getRepository(), branchName);
            boolean isBranchAhead = trackingStatus != null && trackingStatus.getAheadCount() > 0;
            return status.call().isClean() && !isBranchAhead;
        } catch (Exception e) {
            log.error("isClean fail", e);
            throw new RuntimeException(e.getMessage());
        }
    }

}