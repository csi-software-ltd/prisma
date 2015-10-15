class ZalogtypeSearch {
  static mapping = { version false }
  def searchService

  Integer id
  String name

  def csiSelectZalogtype(sName){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='zalogtype'
    hsSql.where="1=1"+
              ((sName!='')?' AND name like concat("%",:name,"%")':'')
    hsSql.order="name asc"

    if(sName!='')
      hsString['name']=sName

    searchService.fetchData(hsSql,null,null,hsString,null,ZalogtypeSearch.class)
  }

}