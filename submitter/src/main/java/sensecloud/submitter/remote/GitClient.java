package sensecloud.submitter.remote;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Data
@Component
public class GitClient {

    private UsernamePasswordCredentialsProvider provider;

    private String localRepo;

    private Git git;

    private String currentBranch;

    private String remoteUri;

    private String remote;

    public boolean init (String username, String password, String localRepo, String remoteUri, String remote) {
        this.provider = new UsernamePasswordCredentialsProvider(username, password);
        this.localRepo = localRepo;
        this.remoteUri = remoteUri;
        this.remote = remote;
        return this.open();
    }


    public void clone(String branch) {
        try {
            this.git = Git.cloneRepository()
                    .setCredentialsProvider(this.provider)
                    .setURI(this.remoteUri)
                    .setBranch(branch)
                    .setDirectory(new File(this.localRepo))
                    .setCloneSubmodules(true)
                    .call();
            this.currentBranch = branch;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public boolean open() {
        boolean flag = false;
        try {
            File repo = new File(this.localRepo + "/.git");
            if(repo.exists()) {
                this.git = Git.open(repo);
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    public boolean addFile(File file) {
        boolean flag = false;
        try {

            String workTreePath = git.getRepository().getWorkTree().getCanonicalPath();
            String pagePath = file.getCanonicalPath();
            pagePath = pagePath.substring(workTreePath.length());
            pagePath = pagePath.replace(File.separatorChar, '/');
            if (pagePath.startsWith("/")) {
                pagePath = pagePath.substring(1);
            }
            log.info("adding file: {}", pagePath);
            DirCache cache = this.git.add().addFilepattern(pagePath).call();

            log.info("dir cache: {}", cache.getEntryCount());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    public void checkout(String branch) {
        try {
            this.git.checkout().setName("refs/heads/" + branch).setForced(true).call();
            this.currentBranch = branch;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void pull(String branch) {
        try {
            this.git.pull().setRemoteBranchName(branch).setCredentialsProvider(this.provider).call();
            this.currentBranch = branch;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void commit(String comment) {
        try {
            this.git.commit().setMessage(comment).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void push(String remoteBranch) {
        try {
            RefSpec spec = new RefSpec("refs/heads/"+ this.currentBranch +":refs/remotes/origin/" + remoteBranch);
            Iterable<PushResult> results = this.git.push().setRemote(this.remote).setCredentialsProvider(this.provider).call();
            if(results != null) {
                for(PushResult pr : results) {
                    log.info("push result: {}", pr.getMessages());
                }
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
