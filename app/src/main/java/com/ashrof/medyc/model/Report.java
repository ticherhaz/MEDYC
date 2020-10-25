package com.ashrof.medyc.model;

public class Report {
    private String reportUid, debit, credit, narration, onCreatedDate, attachedUrl;
    private double amount;

    public Report(String reportUid, String debit, String credit, String narration, String onCreatedDate, String attachedUrl, double amount) {
        this.reportUid = reportUid;
        this.debit = debit;
        this.credit = credit;
        this.narration = narration;
        this.onCreatedDate = onCreatedDate;
        this.attachedUrl = attachedUrl;
        this.amount = amount;
    }

    public Report() {
    }

    public String getReportUid() {
        return reportUid;
    }

    public void setReportUid(String reportUid) {
        this.reportUid = reportUid;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getOnCreatedDate() {
        return onCreatedDate;
    }

    public void setOnCreatedDate(String onCreatedDate) {
        this.onCreatedDate = onCreatedDate;
    }

    public String getAttachedUrl() {
        return attachedUrl;
    }

    public void setAttachedUrl(String attachedUrl) {
        this.attachedUrl = attachedUrl;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
