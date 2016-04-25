class PayrequestClientSearch {
  def searchService
  static mapping = { version false }
//////////////payrequest///////////////////
  Integer id
  Date paydate
  BigDecimal summa
  BigDecimal summands
  String fromcompany
  Integer fromcompany_id
  String tocompany
  Integer tocompany_id
  Integer paytype
  Integer modstatus
  Integer taskpay_id
  Long initiator
  String tagcomment
  Integer project_id
  Integer client_id
  Integer subclient_id
  Integer percenttype
  Double compercent
  Double subcompercent
  Double midpercent
  Double supcompercent
  BigDecimal comission
  BigDecimal subcomission
  BigDecimal midcomission
  BigDecimal supcomission
  BigDecimal clientcommission
  BigDecimal agentcommission
  BigDecimal clientdelta
  Integer confirmstatus
  Integer is_clientcommission
  Integer is_midcommission
  String sfactura
  Date sfacturadate
  Integer expensetype_id
  Integer agentagr_id
  Integer agent_id
  Integer deal_id
  Integer is_bankmoney
  Double payoffperc
  BigDecimal payoffsumma
  Long file_id
  Integer related_id
//////////////Company/////////////////////
  String fromcompany_name
  String tocompany_name
  String client_name
  String subclient_name
  BigDecimal curclientsaldo
  BigDecimal dinclientsaldo

  def csiSelectPayments(iClientId,sCompanyName,iRepayments,dPaydateStart,dPaydateEnd,iStatus,iComission,iTypeId,iNoInner,iSubClientId,iId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="p.*, c1.name as fromcompany_name, c2.name as tocompany_name, if(p.subclient_id=0,cl1.saldo+cl1.addsaldo,cl2.saldo+cl2.addsaldo) as curclientsaldo, cl1.name as client_name, cl2.name as subclient_name, if(p.client_id=0,null,if(p.confirmstatus,0,ifnull(if(p.subclient_id=0,(select sum(if(payrequest.paytype in (1,3,4,7,11),-payrequest.clientdelta,payrequest.clientdelta)) from payrequest where payrequest.client_id=p.client_id and agent_id=0 and subclient_id=0 and payrequest.modstatus>=0 and (payrequest.paydate<p.paydate or (payrequest.paydate=p.paydate and payrequest.id<p.id))),(select sum(if(payrequest.paytype in (1,3,4,7,11),-payrequest.clientdelta,payrequest.clientdelta)) from payrequest where payrequest.subclient_id=p.subclient_id and agent_id=0 and payrequest.modstatus>=0 and (payrequest.paydate<p.paydate or (payrequest.paydate=p.paydate and payrequest.id<p.id)))),0))) as dinclientsaldo"
    hsSql.from='payrequest p left join company c1 on (p.fromcompany_id=c1.id) left join company c2 on (p.tocompany_id=c2.id) left join client cl1 on (p.client_id=cl1.id) left join client cl2 on (p.subclient_id=cl2.id)'
    hsSql.where="p.agent_id=0 and p.modstatus>=0"+
                ((iClientId>0)?' and p.client_id=:client_id':(iClientId==-1)?' and p.client_id>0':'')+
                ((sCompanyName!='')?' and (c1.name like concat("%",:company_name,"%") or c2.name like concat("%",:company_name,"%"))':'')+
                ((iRepayments>0)?' and p.clientcommission>0':'')+
                (dPaydateStart?' and p.paydate>=:paydatestart':'')+
                (dPaydateEnd?' and p.paydate<=:paydateend':'')+
                (iComission>0?' and p.comission>0':'')+
                (iNoInner>0?' and p.paytype!=3':'')+
                (iTypeId>-100?' and p.paytype=:ptype':'')+
                (iSubClientId>0?' and p.subclient_id=:subclient_id':'')+
                (iId>0?' and p.id=:clid':'')+
                ((iStatus==1)?' and p.modstatus in (0,1)':(iStatus==2)?' and p.modstatus>=2':'')
    hsSql.order="p.paydate desc, p.id desc"

    if(sCompanyName!='')
      hsString['company_name'] = sCompanyName
    if(iClientId>0)
      hsLong['client_id'] = iClientId
    if(iSubClientId>0)
      hsLong['subclient_id'] = iSubClientId
    if(iId>0)
      hsLong['clid'] = iId
    if(iTypeId>-100)
      hsLong['ptype'] = iTypeId
    if(dPaydateStart)
      hsString['paydatestart'] = String.format('%tF',dPaydateStart)
    if(dPaydateEnd)
      hsString['paydateend'] = String.format('%tF',dPaydateEnd)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'p.id',true,PayrequestClientSearch.class)
  }

  def csiSelectTPayments(iClientId,sCompanyName,iDeal,dPaydateStart,dPaydateEnd,iStatus,iComission,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="p.*, c1.name as fromcompany_name, c2.name as tocompany_name, if(p.subclient_id=0,cl1.saldo+cl1.addsaldo,cl2.saldo+cl2.addsaldo) as curclientsaldo, cl1.name as client_name, cl2.name as subclient_name, if(p.client_id=0,null,if(p.confirmstatus,0,ifnull(if(p.subclient_id=0,(select sum(if(payrequest.paytype in (1,3,4,7,11),-payrequest.clientdelta,payrequest.clientdelta)) from payrequest where payrequest.client_id=p.client_id and agent_id=0 and subclient_id=0 and payrequest.modstatus>=0 and (payrequest.paydate<p.paydate or (payrequest.paydate=p.paydate and payrequest.id<p.id))),(select sum(if(payrequest.paytype in (1,3,4,7,11),-payrequest.clientdelta,payrequest.clientdelta)) from payrequest where payrequest.subclient_id=p.subclient_id and agent_id=0 and payrequest.modstatus>=0 and (payrequest.paydate<p.paydate or (payrequest.paydate=p.paydate and payrequest.id<p.id)))),0))) as dinclientsaldo"
    hsSql.from='payrequest p left join company c1 on (p.fromcompany_id=c1.id) left join company c2 on (p.tocompany_id=c2.id) left join client cl1 on (p.client_id=cl1.id) left join client cl2 on (p.subclient_id=cl2.id)'
    hsSql.where="p.paytype!=3 and p.paytype!=10 and p.paytype!=11 and p.agent_id=0 and p.modstatus>=0"+
                ((iClientId>0)?' and (p.client_id=:client_id or (p.paytype=2 and p.client_id=0 and is_bankmoney=0))':(iClientId==-1)?' and p.client_id>0':'')+
                ((sCompanyName!='')?' and (c1.name like concat("%",:company_name,"%") or c2.name like concat("%",:company_name,"%"))':'')+
                ((iDeal>0)?' and p.deal_id=0':'')+
                (dPaydateStart?' and p.paydate>=:paydatestart':'')+
                (dPaydateEnd?' and p.paydate<=:paydateend':'')+
                (iComission>0?' and p.comission>0':'')+
                ((iStatus==1)?' and p.modstatus in (0,1)':(iStatus==2)?' and p.modstatus>=2':'')
    hsSql.order="p.paydate desc, p.id desc"

    if(sCompanyName!='')
      hsString['company_name'] = sCompanyName
    if(iClientId>0)
      hsLong['client_id'] = iClientId
    if(dPaydateStart)
      hsString['paydatestart'] = String.format('%tF',dPaydateStart)
    if(dPaydateEnd)
      hsString['paydateend'] = String.format('%tF',dPaydateEnd)

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'p.id',true,PayrequestClientSearch.class)
  }

  def csiSelectDealPayments(iDealId,iClientId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="p.*, c1.name as fromcompany_name, c2.name as tocompany_name, '' as client_name, cl2.name as subclient_name, if(p.client_id=0,null,if(p.confirmstatus,0,ifnull(if(p.subclient_id=0,(select sum(if(payrequest.paytype in (1,3,4,7,11),-payrequest.clientdelta,payrequest.clientdelta)) from payrequest where payrequest.client_id=p.client_id and agent_id=0 and subclient_id=0 and payrequest.modstatus>=0 and (payrequest.paydate<p.paydate or (payrequest.paydate=p.paydate and payrequest.id<p.id))),(select sum(if(payrequest.paytype in (1,3,4,7,11),-payrequest.clientdelta,payrequest.clientdelta)) from payrequest where payrequest.subclient_id=p.subclient_id and agent_id=0 and payrequest.modstatus>=0 and (payrequest.paydate<p.paydate or (payrequest.paydate=p.paydate and payrequest.id<p.id)))),0))) as dinclientsaldo, 0 as curclientsaldo"
    hsSql.from='payrequest p left join company c1 on (p.fromcompany_id=c1.id) left join company c2 on (p.tocompany_id=c2.id) left join client cl2 on (p.subclient_id=cl2.id)'
    hsSql.where="p.paytype!=3 and agent_id=0 and p.modstatus>1 and subclient_id=0"+
                ((iClientId>0)?' and p.client_id=:client_id':'')+
                ((iDealId>0)?' and p.deal_id=:deal_id':' and p.deal_id=0')
    hsSql.order="p.paydate desc, p.id desc"

    if(iDealId>0)
      hsLong['deal_id'] = iDealId
    if(iClientId>0)
      hsLong['client_id'] = iClientId

    searchService.fetchData(hsSql,hsLong,null,null,null,PayrequestClientSearch.class)
  }

  BigDecimal computeClientdelta(){
    return (confirmstatus?0.0g:paytype in [1,3,4,7,11]?-clientdelta:clientdelta)
  }

  BigDecimal computeIncome(){
    return (paytype in [2,5,8,9,10]&&!is_clientcommission&&!is_midcommission?clientdelta:0.0g)
  }

  BigDecimal computeOutlay(){
    return (paytype in [1,7,11]&&!is_clientcommission&&!is_midcommission?clientdelta:0.0g)
  }
}