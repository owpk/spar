package ru.sparural.gradle.plugins.microservice.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.Project;

public class ProjectVersionChecker {

    private final Git git;

    public ProjectVersionChecker(Project project) {
        Git tmpGit;
        try {
            tmpGit = Git.open(project.getProjectDir());
        } catch (IOException ex) {
            tmpGit = null;
        }
        this.git = tmpGit;
    }

    public String getProjectVersion() {
        try {
            if (git == null) {
                return "1.0.0";
            }

            Ref taggedRef = getTaggedRef();
            if (taggedRef == null) {
                return "1.0.0";
            }

            String branch = git.getRepository().getBranch();
            if ("master".equals(branch)) {
                return getMasterVersion(taggedRef);
            } else {
                return getBranchVersion(taggedRef) + "-" + branch + "-SNAPSHOT";
            }
        } catch (IOException ex) {
            return "1.0.0";
        }
    }

    private String getMasterVersion(Ref taggedRef) {
        if (lastTaggedCommit(taggedRef) == 0) {
            return taggedRef.getName().substring(11);
        }

        return new StringBuilder()
                .append(getVersion(taggedRef, 0))
                .append(".")
                .append(getVersion(taggedRef, 1)+1)
                .append(".0")
                .toString();
    }

    private String getBranchVersion(Ref taggedRef) {
        return new StringBuilder()
                .append(getVersion(taggedRef, 0))
                .append(".")
                .append(getVersion(taggedRef, 0))
                .append(".")
                .append(lastTaggedCommit(taggedRef)+1)
                .toString();
    }

    private Ref getTaggedRef() {
        try {

            Ref tagRef = git.tagList().call().stream().sorted(this::compareRefs).findFirst().orElse(null);
            return tagRef!=null ? git.getRepository().peel(tagRef) : null;
        } catch (GitAPIException ex) {
            return null;
        }
    }

    private int compareRefs(Ref ref1, Ref ref2) {
        Long v1 = getLongVersion(ref1);
        Long v2 = getLongVersion(ref2);
        return v2.compareTo(v1);
    }

    private int lastTaggedCommit(Ref tagRef) {
        try {
            if (tagRef == null || tagRef.getPeeledObjectId() == null) {
                return -1;
            }

            String taggedId = tagRef.getPeeledObjectId().getName();

            List<RevCommit> commitList = new ArrayList<>();
            git.log().setMaxCount(50).call().forEach(commitList::add);
            for (int i=0; i<commitList.size(); i++) {
                if (commitList.get(i).getId().getName().equals(taggedId)) {
                    return i;
                }
            }
            return 51;
        } catch (GitAPIException ex) {
            return -1;
        }
    }

    private Integer getVersion(Ref taggedRef, int block) {
        if (taggedRef == null) {
            return 0;
        }

        String[] versions = taggedRef.getName().substring(11).split("\\.");
        if (versions.length<3) {
            return 0;
        }

        return Integer.valueOf(versions[block]);
    }

    private Long getLongVersion(Ref taggedRef) {
        if (taggedRef == null) {
            return 0l;
        }

        String[] versions = taggedRef.getName().substring(11).split("\\.");
        if (versions.length<3) {
            return 0l;
        }

        return Long.valueOf(versions[0])*1000000 + Long.valueOf(versions[1])*1000 + Long.valueOf(versions[2]);
    }
}
