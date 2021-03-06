package com.neon.intellij.plugins.gitlab;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.neon.intellij.plugins.gitlab.model.gitlab.GIPIssue;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetIssuesTask extends Task.Backgroundable {

    private static final Logger LOGGER = Logger.getLogger( GetIssuesTask.class.getName() );

    private final GitLabService gitLabService;

    private final GIPIssueObserver issueObserver;

    private final Integer projectId;

    public GetIssuesTask(@Nullable Project project, GitLabService gitLabService, GIPIssueObserver issueObserver, Integer projectId ) {
        super(project, "Getting Issues From Gitlab", true);
        this.gitLabService = gitLabService;
        this.issueObserver = issueObserver;
        this.projectId = projectId;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        request( 10, 1 );
    }

    private void request( final int limit, final int page ) {
        gitLabService.listProjectIssues( projectId, limit, page ).subscribe(new Observer<List<GIPIssue>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<GIPIssue> projects) {
                if ( projects == null ) {
                    return ;
                }

                projects.forEach( issueObserver::accept );

                if ( projects.size() >= limit ) {
                    request( limit, page + 1 );
                }
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e );
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
