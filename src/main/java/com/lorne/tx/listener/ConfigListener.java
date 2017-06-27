package com.lorne.tx.listener;

import com.lorne.tx.Constants;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by lorne on 2017/6/27.
 */
@Component
public class ConfigListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {




    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        Constants.server_port = event.getEmbeddedServletContainer().getPort();
    }
}
