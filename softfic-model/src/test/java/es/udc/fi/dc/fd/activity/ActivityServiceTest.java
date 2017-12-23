package es.udc.fi.dc.fd.activity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import es.udc.fi.dc.fd.activity.entities.Activity;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceTest {

	@InjectMocks
	private ActivityService activityService = Mockito.spy(new ActivityService());

	@Mock
	private ActivityRepository activityRepository;

	@Test
	public void findActivityFeedTest() {
		List<Activity> list = new ArrayList<Activity>();
		Page<Activity> timeline = new PageImpl<Activity>(list, new PageRequest(0, 10), 0);
		when(activityRepository.findActivitiesByUserFriends(2L, new PageRequest(0, 10))).thenReturn(timeline);
		assertEquals(timeline, activityService.findActivityFeed(2L, 0));
	}
	
}
