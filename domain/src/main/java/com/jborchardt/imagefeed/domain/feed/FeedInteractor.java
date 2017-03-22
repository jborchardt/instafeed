package com.jborchardt.imagefeed.domain.feed;

import android.support.annotation.NonNull;

import com.jborchardt.imagefeed.domain.common.Interactor;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by johannesborchardt on 21.03.17.
 */

public class FeedInteractor extends Interactor<FeedItemModel> {

    private String MIME_TYPE_JPEG = "image/jpeg";
    private String MIME_TYPE_PNG = "image/png";

    private final FeedRepository mFeedRepository;
    private int mPage;
    private List<FeedItemModel> mFeedItems;

    public FeedInteractor(final Scheduler executionScheduler, final Scheduler postExecutionScheduler, final FeedRepository feedRepository) {
        super(executionScheduler, postExecutionScheduler);

        mFeedRepository = feedRepository;
        mFeedItems = new ArrayList<>();
    }

    public void fetchFirstFeedPage(@NonNull final DisposableObserver<FeedItemModel> observer) {
        fetchFeed(observer, 0);
    }

    public void fetchNextPage(@NonNull final DisposableObserver<FeedItemModel> observer) {
        mPage++;
        fetchFeed(observer, mPage);
    }

    private void fetchFeed(@NonNull final DisposableObserver<FeedItemModel> observer, final int page) {
        final Observable<FeedItemModel> feedObservable = Observable.create(emitter -> {
            try {
                final List<? extends FeedItemModel> feedItems = mFeedRepository.fetchFeed(page);
                for(FeedItemModel feedItem : feedItems) {
                    if(isAcceptedType(feedItem)) {
                        emitter.onNext(feedItem);
                    }
                }

                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

        execute(observer, feedObservable);
    }

    private boolean isAcceptedType(final FeedItemModel feedItem) {
        return feedItem != null && (MIME_TYPE_JPEG.equals(feedItem.getType()) || MIME_TYPE_PNG.equals(feedItem.getType()));
    }
}