package com.api.apigetsweetwords.controller;

import com.wxy.api.sdk.utils.GatewayHeaderUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 *  随机一段情话
 */
@RestController
@RequestMapping("/sweet")
public class SweetWordsController
{
    @GetMapping("/getWords")
    public String SweetWordsGenerator(HttpServletRequest request){
        GatewayHeaderUtils.validateGatewayHeaders(request);
        // 定义一组情话
        String[] sweetWords = {
                "遇见你，是我一生的幸运；爱上你，是我做过最好的事情。",
                "如果可以，我愿意和你一起看遍世间所有美景。",
                "你是我心中永远的春天，温暖而明媚。",
                "无论走到哪里，我的心永远为你停留。",
                "你是我生命中的阳光，没有你，我的世界一片黑暗。",
                "这一生最幸运的事，就是和你一起书写我们的故事。",
                "世间的温柔数不尽，但唯有你是我的偏爱。",
                "爱上你之后，我才明白什么是心跳的感觉。",
                "世界再大，我的眼中也只有你。",
                "想陪你走过四季春秋，直到天荒地老。"
        };

        // 创建随机数生成器
        Random random = new Random();

        // 随机选择一句情话
        int index = random.nextInt(sweetWords.length);
        String randomSweetWord = sweetWords[index];

        return randomSweetWord;
    }
}
