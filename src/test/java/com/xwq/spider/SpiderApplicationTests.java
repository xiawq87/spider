package com.xwq.spider;

import com.xwq.spider.sp.yousuu.YousuuTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpiderApplicationTests {
	@Autowired
	YousuuTask yousuuTask;

	@Test
	public void test() {
		yousuuTask.doTask();
	}
}
