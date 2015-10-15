class Usermenu implements Serializable{
  def searchService

  static constraints = {
  }

  static mapping = {
    version false
  }
  private static final long serialVersionUID = 1L;

  Integer id
  String name
  String controller
  String action
  Integer is_main
  String checkfield

  def csiGetMenu(iGroupId){
    def oUsergroup = Usergroup.get(iGroupId)
    def lsMenuItemIds = []
    
    if(oUsergroup.menu.size()){
      lsMenuItemIds = oUsergroup.menu.tokenize(',')
      
      def hsSql = [select :'*',
                 from   :'usermenu',
                 where  :'id in (:ids)',
                 order  :'id']
      def hsList = [ids:lsMenuItemIds]
      return searchService.fetchData(hsSql,null,null,null,hsList,Usermenu.class)
    }else
      return []
  }
}