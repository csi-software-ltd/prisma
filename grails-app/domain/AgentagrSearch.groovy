class AgentagrSearch {
  def searchService
  static mapping = {
    version false
  }
/////////agentagr////////////////////
  Integer id
  String name
  Integer client_id
  Date inputdate
  Integer modstatus
/////////general/////////////////////
  Integer kreditcount
  BigDecimal sumrub
  BigDecimal sumusd
  BigDecimal sumeur
  BigDecimal lastbodydebt

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectAgentagrs(iId,iClientId,sBankname,sNumber,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="agentagr.*,count(distinct kredit.id) as kreditcount, sum(kredit.summa), sum(if(kredit.valuta_id=857,kredit.agentsum,0)) as sumrub, sum(if(kredit.valuta_id=840,kredit.agentsum,0)) as sumusd, sum(if(kredit.valuta_id=978,kredit.agentsum,0)) as sumeur, sum(ifnull(agentkreditplan.debt,0)) as lastbodydebt"
    hsSql.from='agentagr left join agentkredit on (agentagr.id=agentkredit.agentagr_id) left join kredit on (agentkredit.kredit_id=kredit.id) left join agentkreditplan on (agentkredit.id=agentkreditplan.agentkredit_id and agentkreditplan.modstatus<2 and is_last=1)'
    hsSql.where="1=1"+
                ((iId>0)?' AND agentagr.id =:agentagr_id':'')+
                ((iClientId>0)?' AND agentagr.client_id =:client_id':'')+
                (sBankname!=''?' AND (select count(*) from agentagrbank join bank b on (agentagrbank.bank_id=b.id) where agentagrbank.agentagr_id=agentagr.id and b.name like concat("%",:bankname,"%"))> 0':'')+
                ((sNumber!='')?' AND kredit.anumber =:anumber':'')+
                ((iStatus>0)?' AND agentagr.modstatus = 1':' AND agentagr.modstatus = 0')
    hsSql.group="agentagr.id"
    hsSql.order="agentagr.id desc"

    if(iId>0)
      hsLong['agentagr_id']=iId
    if(iClientId>0)
      hsLong['client_id']=iClientId
    if(sBankname!='')
      hsString['bankname']=sBankname
    if(sNumber!='')
      hsString['anumber']=sNumber

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'*',true,AgentagrSearch.class)
  }

}