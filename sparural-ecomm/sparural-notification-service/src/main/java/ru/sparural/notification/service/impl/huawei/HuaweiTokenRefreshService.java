package ru.sparural.notification.service.impl.huawei;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.notification.service.impl.huawei.rest.HuaweiRestClient;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HuaweiTokenRefreshService {
    private final HuaweiRestClient restClient;

    private ScheduledExecutorService executorService;
    private HuaweiServerToken huaweiServerToken;
    private ScheduledFuture<?> future;

    @PostConstruct
    private void init() {
        var builder = new ThreadFactoryBuilder()
                .setThreadFactory(Executors.defaultThreadFactory())
                .setNameFormat("huawei-scheduled-service")
                .setDaemon(true)
                .build();
        this.executorService = Executors
                .newSingleThreadScheduledExecutor(builder);
    }

    public void scheduleRefresh() {
        log.debug("huawei refresh token initialized");
        cancelPrevious();
        scheduleNext();
    }

    public synchronized HuaweiServerToken retrieveServerToken() {
        scheduleRefresh();
        return huaweiServerToken;
    }

    private void scheduleNext() {
        if (huaweiServerToken == null ||
                this.huaweiServerToken.getAccessToken() == null) {
            refreshToken();
        }
        future = executorService.scheduleWithFixedDelay(
                this::refreshToken, huaweiServerToken.getExpiresIn(),
                huaweiServerToken.getExpiresIn(), TimeUnit.MILLISECONDS);
    }

    private void cancelPrevious() {
        if (future != null)
            future.cancel(true);
    }

    public void refreshToken() {
        var token = restClient.refreshToken();
        huaweiServerToken = new HuaweiServerToken();
        huaweiServerToken.setAccessToken(token.getAccessToken());
        huaweiServerToken.setExpiresIn(token.getExpiresIn());
    }

}