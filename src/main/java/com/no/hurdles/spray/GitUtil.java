package com.no.hurdles.spray;

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
import org.eclipse.jgit.api.ResetCommand;

import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * git操作工具
 * 更多api参考：https://github.com/centic9/jgit-cookbook
 */
@Slf4j
public class GitUtil {

    public static void main(String[] args) throws Exception {

        //repositoryInit();

        //System.out.println(checkout("testbranch"));

        //createLocalBranch("chenwei222");
        //deleteLocalBranch("chenwei");

        //reateOriginBranch("kkkkkk");
        //deleteOriginBranch("chenwei666");
        System.out.println(getLocalBranchList());
        System.out.println("===========================");
        System.out.println(getOriginBranchList());
        System.out.println("===========================");

    }

    private final static String GIT = ".git";
    //定义本地git工作目录
    public static final String BaseDir = "D:/spraywork" + File.separator;
    //.git文件路径
    public static final String RepositoryName = "demo";
    //远程仓库地址
    public static final String githubUrl = "https://gitlab.com/1041579785/demo.git";

    private static final String LOCAL_BRANCH_REF_PREFIX = "refs/remotes/origin/";

    //操作git的用户名&密码
    public static final String USER = "1041579785";
    public static final String PASSWORD = "qwertyuiop";

    /**
     * 初始化仓库，只需要执行一次
     * @return
     */
    public static boolean repositoryInit() {
        try {
            Git.cloneRepository()
               .setURI(githubUrl)
               .setCredentialsProvider(new UsernamePasswordCredentialsProvider(USER, PASSWORD))
               .setCloneAllBranches(true)
               .setDirectory(new File(BaseDir+RepositoryName))
               .call();
            return true;
        } catch (Exception e) {
            log.error("repositoryInit fail", e);
            return false;
        }
    }

    /**
     * 获取远端分支列表
     * @return
     */
    public static List<Ref> getOriginBranchList() throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        List<Ref> list = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        return list;
    }

    /**
     * 获取本地分支列表
     * @return
     */
    public static List<Ref> getLocalBranchList() throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        return git.branchList().call();
    }

    /**
     * 创建远端分支
     * @param branchName
     * @throws IOException
     * @throws GitAPIException
     */
    public static void createOriginBranch(String branchName) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        //创建本地分支
        git.branchCreate().setName(branchName).setForce(true).call();
        //推送远端分支
        git.push()
                .setRefSpecs(new RefSpec(branchName + ":" + branchName))// local:origin
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(USER,PASSWORD))
                .call();
    }

    /**
     * 创建本地分支
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static Ref createLocalBranch(String branchName) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        return git.branchCreate().setName(branchName).setForce(true).call();
    }

    /**
     * 删除远程分支
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static void deleteOriginBranch(String branchName) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        git.push().setRefSpecs(new RefSpec(":refs/heads/" + branchName)).setCredentialsProvider(new UsernamePasswordCredentialsProvider(USER, PASSWORD)).call();
    }

    /**
     * 删除本地分支
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public static List<String> deleteLocalBranch(String branchName) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        return git.branchDelete().setBranchNames(branchName).call();
    }

    /**
     *
     * <p>
     * Description:判断本地分支名是否存在
     * </p>
     *
     * @param branchName
     * @return
     * @throws GitAPIException
     * @author wgs
     * @date 2019年7月20日 下午2:49:46
     *
     */
    public static boolean branchNameExist(String branchName) throws GitAPIException, IOException {
        List<Ref> refs = getLocalBranchList();
        for (Ref ref : refs) {
            if (ref.getName().contains(branchName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check分支到本地
     * @param label
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public static Ref checkout(String label) throws GitAPIException, IOException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        CheckoutCommand checkout = git.checkout();
        if (shouldTrack(git, label)) {
            checkout.setCreateBranch(true)
                    .setName(label)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/" + label);
        } else {
            checkout.setName(label);
        }
        return checkout.call();
    }

    private static boolean containsBranch(Git git, String label, ListBranchCommand.ListMode listMode) throws GitAPIException {
        ListBranchCommand command = git.branchList();
        if (listMode != null) {
            command.setListMode(listMode);
        }
        List<Ref> branches = command.call();
        for (Ref ref : branches) {
            if (ref.getName().endsWith("/" + label)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBranch(Git git, String label) throws GitAPIException {
        return containsBranch(git, label, ListBranchCommand.ListMode.ALL);
    }

    private static boolean isLocalBranch(Git git, String label) throws GitAPIException {
        return containsBranch(git, label, null);
    }

    private static boolean shouldTrack(Git git, String label) throws GitAPIException {
        return isBranch(git, label) && !isLocalBranch(git, label);
    }

    /**
     * 合并分支
     * @param label
     */
    private static void tryMerge(String label) throws IOException, GitAPIException {
        Git git = new Git(new FileRepository(BaseDir + RepositoryName + File.separator + GIT));
        if (isBranch(git, label)) {
            // merge results from fetch
            merge(git, label);
            if (!isClean(git, label)) {
                log.warn("The local repository is dirty or ahead of origin. Resetting" + " it to origin/" + label + ".");
                resetHard(git, label, LOCAL_BRANCH_REF_PREFIX + label);
            }
        }
    }

    private static MergeResult merge(Git git, String label) throws IOException, GitAPIException {
        MergeCommand merge = git.merge();
        merge.include(git.getRepository().findRef("origin/" + label));
        MergeResult result = merge.call();
        if (!result.getMergeStatus().isSuccessful()) {
            log.warn("Merged from remote " + label + " with result " + result.getMergeStatus());
        }
        return result;
    }

    private static Ref resetHard(Git git, String label, String ref) throws GitAPIException {
        ResetCommand reset = git.reset();
        reset.setRef(ref);
        reset.setMode(ResetCommand.ResetType.HARD);
        Ref resetRef = reset.call();
        if (resetRef != null) {
            log.info("Reset label " + label + " to version " + resetRef.getObjectId());
        }
        return resetRef;
    }

    private static boolean isClean(Git git, String label) throws GitAPIException, IOException {
        StatusCommand status = git.status();
        BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(git.getRepository(), label);
        boolean isBranchAhead = trackingStatus != null && trackingStatus.getAheadCount() > 0;
        return status.call().isClean() && !isBranchAhead;
    }

    protected FetchResult fetch(Git git, String label) throws GitAPIException {
        FetchCommand fetch = git.fetch();
        fetch.setRemote("origin");
        fetch.setTagOpt(TagOpt.FETCH_TAGS);
        fetch.setRemoveDeletedRefs(true);
        FetchResult result = fetch.call();
        if (result.getTrackingRefUpdates() != null && result.getTrackingRefUpdates().size() > 0) {
            log.info("Fetched for remote " + label + " and found " + result.getTrackingRefUpdates().size()
                     + " updates");
        }
        return result;
    }

    /**
     * Deletes local branches if corresponding remote branch was removed.
     * @param trackingRefUpdates list of tracking ref updates
     * @param git git instance
     * @return list of deleted branches
     */
    private static Collection<String> deleteUntrackedLocalBranches(Collection<TrackingRefUpdate> trackingRefUpdates, Git git)
            throws GitAPIException, IOException {
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
        // make sure that deleted branch not a current one
        checkout("当前分支");
        return deleteBranches(git, branchesToDelete);
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


}  