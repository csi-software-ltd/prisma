class Userlog {
  
  def searchService
  def sessionFactory
  
  static constraints = {
    success_sms(nullable:true)
  }
  static mapping = {    
    version false
  }
  Long id
  Long user_id
  Date logtime
  String ip
  Integer success
  Integer success_duration
  Integer success_sms
  
  def csiGetLogs(lId){
    def hsSql = [select :'distinct *',
                 from   :'userlog',
                 where  :'user_id = :id AND success=1',
                 order  :'logtime desc']
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null,Userlog.class)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessLogs(lId, dDateFrom){
    def sDateFrom = String.format('%tY-%<tm-%<td %<tH:%<tM:%<tS', dDateFrom)
    def hsSql = [select :'count(id)',
                 from   :'userlog',
                 where  :"user_id = :id AND success=0 AND logtime>'${sDateFrom}'"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  def resetSuccessDuration(lUserId){
    def session = sessionFactory.getCurrentSession()
    def sSql = "UPDATE userlog SET success_duration=1 WHERE user_id=:id AND success_duration=0"
    def qSql = session.createSQLQuery(sSql)
    qSql.setLong('id',lUserId)
    qSql.executeUpdate()
    session.clear()
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessDurationLogs(lId){  
    def hsSql = [select :'count(id)',
                 from   :'userlog',
                 where  :"user_id = :id AND success_duration=0"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessSmsLogs(lId, dDateFrom){
    def sDateFrom = String.format('%tY-%<tm-%<td %<tH:%<tM:%<tS', dDateFrom)
    def hsSql = [select :'count(id)',
                 from   :'userlog',
                 where  :"user_id = :id AND success_sms=0 AND logtime>'${sDateFrom}'"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  //////////////////////////////////////////////////////////////////////////////////////
  def csiGetUserLog(lId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]        

    hsSql.select="*"
    hsSql.from="userlog"     
    hsSql.where="1=1"+
      ((lId>0)?' AND user_id=:lId':'')      
                
    hsSql.order="id desc"    
    
    if(lId>0)
      hsLong['lId']=lId    
//log.debug(hsSql)
    return searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Userlog.class)
  } 
}
