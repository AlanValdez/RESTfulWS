package me.restfulws.utm.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import me.restfulws.utm.model.Notification;
import me.restfulws.utm.repository.NotificationRepository;

@Service
public class NotificationServiceImpl implements NotificationService {
	@Autowired
	private MailSender mailSender;
	@Autowired
	NotificationRepository notificationRepository;
	
	private static final Logger logger = LogManager.getLogger();

	@Override
	public List<Notification> getNotifications() {
		return notificationRepository.getNotifications();
	}

	@Async
	@Override
	public Notification notify(String subject, String message, List<String> toAddress, List<String> ccAddress) {
		Notification notification = new Notification();
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		String threadName = Thread.currentThread().getName();
		logger.info("{} started subject={}, message={}, toAddress={}, ccAddress={}", threadName, subject, message, toAddress, ccAddress);

		notification.setSubject(subject);
		notification.setMessage(message);
		notification.setToAddress(toAddress);
		notification.setCcAddress(ccAddress);

		try {
			SimpleMailMessage emailMessage = new SimpleMailMessage();
			emailMessage.setFrom("puchipupul@hotmail.com");
			emailMessage.setTo(String.join(",", toAddress));
			if (ccAddress.size() > 0 && !ccAddress.get(0).isEmpty())
				emailMessage.setCc(String.join(",", ccAddress));
			emailMessage.setSubject(subject);
			emailMessage.setText(message);
			mailSender.send(emailMessage);
			notification.setStatus("SENT");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			notification.setStatus("ERROR");
		}
		stopwatch.stop();
		logger.info("{} took {} secs", threadName, stopwatch.getTotalTimeSeconds());
		notificationRepository.addNotification(notification);
		return notification;
	}
}