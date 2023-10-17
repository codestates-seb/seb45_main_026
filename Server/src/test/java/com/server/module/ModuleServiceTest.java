package com.server.module;

import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.video.repository.VideoRepository;
import com.server.module.redis.service.RedisService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ModuleServiceTest {

    @MockBean protected VideoRepository mockVideoRepository;
    @MockBean protected ChannelRepository mockChannelRepository;
}
