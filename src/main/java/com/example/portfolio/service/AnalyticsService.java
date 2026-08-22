package com.example.portfolio.service;

import com.example.portfolio.entity.SiteStat;
import com.example.portfolio.repository.SiteStatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    private final SiteStatRepository siteStatRepository;

    public AnalyticsService(SiteStatRepository siteStatRepository) {
        this.siteStatRepository = siteStatRepository;
    }

    private SiteStat todayStat() {
        LocalDate today = LocalDate.now();
        return siteStatRepository.findByStatDate(today).orElseGet(() -> {
            SiteStat s = new SiteStat();
            s.setStatDate(today);
            return siteStatRepository.save(s);
        });
    }

    public synchronized void recordPortfolioView() {
        SiteStat s = todayStat();
        s.setPortfolioViews(s.getPortfolioViews() + 1);
        siteStatRepository.save(s);
    }

    public synchronized void recordResumeDownload() {
        SiteStat s = todayStat();
        s.setResumeDownloads(s.getResumeDownloads() + 1);
        siteStatRepository.save(s);
    }

    public synchronized void recordMessageReceived() {
        SiteStat s = todayStat();
        s.setMessagesReceived(s.getMessagesReceived() + 1);
        siteStatRepository.save(s);
    }

    public synchronized void recordProjectClick() {
        SiteStat s = todayStat();
        s.setProjectClicks(s.getProjectClicks() + 1);
        siteStatRepository.save(s);
    }

    public List<SiteStat> last30Days() {
        return siteStatRepository.findTop30ByOrderByStatDateDesc();
    }
}
