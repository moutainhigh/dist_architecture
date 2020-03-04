import com.xpay.common.statics.constants.rmqdest.NotifyTestDest;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.notify.entity.NotifyRecord;
import com.xpay.facade.notify.service.NotifyFacade;
import com.xpay.facade.notify.service.NotifyManageFacade;
import com.xpay.service.notify.ServiceNotifyApp;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 16:12
 * Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceNotifyApp.class)
public class TestNotify {
    @Autowired
    private NotifyFacade notifyFacade;

    @Autowired
    private NotifyManageFacade notifyManageFacade;

    @Test
    @Ignore
    public void test1() {
        Map<String, Date> map = new HashMap<>();
        map.put("birth", new Date());
        for (int i = 10005; i < 10006; i++) {
            notifyFacade.sendNotify("888", NotifyTestDest.NOTIFY_TYPE_TEST, map, "" + i, "222", "333");
        }
    }


    @Test
    @Ignore
    public void test2() {
        final PageResult<List<NotifyRecord>> result = notifyManageFacade.listNotifyRecordPage(new HashMap<>(), PageParam.newInstance(1, 100));
        System.out.println(result);

    }
}
