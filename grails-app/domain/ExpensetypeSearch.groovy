class ExpensetypeSearch {
  def searchService
  static mapping = { version false }

  Integer id
  String name
  String razdel
  String podrazdel
  Integer type
  Integer modstatus
  Integer expensetype1_id
  Integer expensetype2_id

  String razdel_name

  def csiGetRazdel(){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']

    hsSql.select="*, expensetype1.name as razdel_name"
    hsSql.from="expensetype, expensetype1"
    hsSql.where="expensetype.expensetype1_id=expensetype1.id"
    hsSql.order="expensetype1.name asc"
    hsSql.group="expensetype1_id"

    searchService.fetchData(hsSql,null,null,null,null,ExpensetypeSearch.class)
  }

  def csiSelectTypes(iRazdel,iSubRazdel,sName,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]
    
    hsSql.select='*, e.id as id, "" as razdel_name'
    hsSql.from='expensetype as e, expensetype1 as e1, expensetype2 as e2'
    hsSql.where='e.expensetype1_id=e1.id AND e.expensetype2_id=e2.id/* AND e2.expensetype1_id=e1.id*/'+
                ((iRazdel>0)?' AND e.expensetype1_id=:razdel':'')+
                ((iSubRazdel>0)?' AND e.expensetype2_id=:subrazdel':'')+
                (iStatus>-100?' AND e.modstatus=:status':'')+
                ((sName!='')?' and e.name like concat("%",:name,"%")':'')
    hsSql.order="e1.name, e2.name, e.name"

    if(iRazdel>0)
      hsInt['razdel'] = iRazdel
    if(iSubRazdel>0)
      hsInt['subrazdel'] = iSubRazdel
    if(iStatus>-100)
      hsInt['status'] = iStatus
    if(sName!='')
      hsString['name']=sName

    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'e.id',true,ExpensetypeSearch.class)
  }

  def csiGetList(sName='',lUserId=0,iMax=-1,iId=0){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]
    def hsLong=[:]
    def oUser = User.get(lUserId)

    hsSql.select="*"
    hsSql.from="expensetype"
    hsSql.where="(modstatus = 1 or id=:eid)"+
                ((sName!='')?' and (name like concat(:name,"%") or podrazdel like concat(:name,"%"))':'')+
                (!oUser?'':(oUser.cashaccess==1)?' and id in (select expensetype_id from expense2user where user_id=:uid)':(!(oUser.cashaccess in [3,6]))?' and id in (select expensetype_id from expense2dep where department_id=:dep_id)':'')
    hsSql.order="razdel asc, podrazdel asc, name asc"

    hsLong['eid'] = iId
    if(sName!='')
      hsString['name'] = sName
    if(oUser){
      if(oUser.cashaccess==1)
        hsLong['uid'] = oUser.id
      else if(!(oUser.cashaccess in [3,6]))
        hsLong['dep_id'] = oUser.department_id
    }

    searchService.fetchData(hsSql,hsLong,null,hsString,null,Expensetype.class,iMax)
  }

  def csiGetFullList(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*"
    hsSql.from="expensetype"
    hsSql.where="1=1"
    hsSql.order="razdel asc, podrazdel asc, name asc"

    searchService.fetchData(hsSql,null,null,null,null,Expensetype.class)
  }

  def csiGetCarList(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*"
    hsSql.from="expensetype"
    hsSql.where="is_car=1"
    hsSql.order="razdel asc, podrazdel asc, name asc"

    searchService.fetchData(hsSql,null,null,null,null,Expensetype.class)
  }

  def csiGetUserTypes(iUserId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    
    hsSql.select='*, "" as razdel_name'
    hsSql.from='expensetype join expense2user on (expense2user.expensetype_id=expensetype.id)'
    hsSql.where='1=1'+
                ((iUserId>0)?' AND expense2user.user_id=:uId':'')
    hsSql.order="expensetype.id asc"

    if(iUserId>0)
      hsLong['uId'] = iUserId

    searchService.fetchData(hsSql,hsLong,null,null,null,Expensetype.class)
  }

  def csiGetDepartmentTypes(iDepId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    
    hsSql.select='*, "" as razdel_name'
    hsSql.from='expensetype join expense2dep on (expense2dep.expensetype_id=expensetype.id)'
    hsSql.where='1=1'+
                ((iDepId>0)?' AND expense2dep.department_id=:depId':'')
    hsSql.order="expensetype.id asc"

    if(iDepId>0)
      hsLong['depId'] = iDepId

    searchService.fetchData(hsSql,hsLong,null,null,null,Expensetype.class)
  }
}