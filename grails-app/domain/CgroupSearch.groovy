class CgroupSearch {
  static mapping = { version false }
  def searchService

  Integer id
  String name

  def csiSelectCgroup(sName){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='cgroup'
    hsSql.where="1=1"+
              ((sName!='')?' AND name like concat("%",:name,"%")':'')
    hsSql.order="name asc"

    if(sName!='')
      hsString['name']=sName

    searchService.fetchData(hsSql,null,null,hsString,null,CgroupSearch.class)
  }

}