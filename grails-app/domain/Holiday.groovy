class Holiday {
  def searchService
  
  static mapping = {
    version false
  }

  Integer id
  Date hdate
  Integer status
  
  def csiSetHoliday(hsInrequest){
    hdate=Tools.getDate(hsInrequest?.hdate)
    status=hsInrequest?.status?:0
    this
  }
  
  def csiSelectHoliday(iYear){
    def hsSql=[select:'',from:'',where:'',order:'']      
    def hsInt=[:]        

    hsSql.select="*"
    hsSql.from="holiday"     
    hsSql.where="1=1"+
      ((iYear>0)?' AND YEAR(hdate)=:iYear':'')      
      
    hsSql.order="hdate desc"       
    
    if(iYear>0)
      hsInt['iYear']=iYear
    
              
//log.debug(hsSql)
   return searchService.fetchData(hsSql,null,hsInt,null,null,Holiday.class)   
  }
}