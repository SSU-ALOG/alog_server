package com.example.alog.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {

  @Id
  @Column(name = "sn", nullable = false)
  private long msgSn;

  @Column(name = "dst_se_nm", nullable = false)
  private String dstSeNm;

  @Column(name = "crt_dt", nullable = false)
  private java.sql.Timestamp crtDt;

  @Column(name = "msg_cn", nullable = false)
  private String msgCn;

  @Column(name = "rcptn_rgn_nm", nullable = false)
  private String rcptnRgnNm;

  @Column(name = "emrg_step_nm", nullable = false)
  private String emrgStepNm;

  public long getMsgSn() {
    return msgSn;
  }

  public void setMsgSn(long msgSn) {
    this.msgSn = msgSn;
  }


  public String getDstSeNm() {
    return dstSeNm;
  }

  public void setDstSeNm(String dstSeNm) {
    this.dstSeNm = dstSeNm;
  }


  public java.sql.Timestamp getCrtDt() {
    return crtDt;
  }

  public void setCrtDt(java.sql.Timestamp crtDt) {
    this.crtDt = crtDt;
  }


  public String getMsgCn() {
    return msgCn;
  }

  public void setMsgCn(String msgCn) {
    this.msgCn = msgCn;
  }


  public String getRcptnRgnNm() {
    return rcptnRgnNm;
  }

  public void setRcptnRgnNm(String rcptnRgnNm) {
    this.rcptnRgnNm = rcptnRgnNm;
  }


  public String getEmrgStepNm() {
    return emrgStepNm;
  }

  public void setEmrgStepNm(String emrgStepNm) {
    this.emrgStepNm = emrgStepNm;
  }

}
