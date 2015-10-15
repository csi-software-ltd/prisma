class UserpersSearch {
  def searchService
  static mapping = { version false }

  Long id
  Long pers_id
  Integer is_remote
  Integer is_block
  Integer modstatus
  Integer usergroup_id
  Integer department_id
  Integer accesslevel
  Integer cashaccess
  Integer confaccess
  String login
	String name
  String email
	String password
	String tel
  String smscode
  Date inputdate
  Date lastdate
  Long saldo
  Long cassadebt
  Long precassadebt
  Integer is_leader
  Integer is_loan
  Long loansaldo
  Long penalty

  String pers_name
  String dep_name
  Long actsalary
  String group_name

  def csiFindByCashaccess(liCashacess){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsList=[:]

    hsSql.select="*, pers.shortname as pers_name, '' as dep_name, '' as group_name"
    hsSql.from='user left join pers on (user.pers_id=pers.id)'
    hsSql.where="user.usergroup_id!=1"+
                (liCashacess?' AND user.cashaccess in (:cashaccess)':'')
    hsSql.order="cashaccess desc, name asc"

    if(liCashacess)
      hsList['cashaccess']=liCashacess

    searchService.fetchData(hsSql,null,null,null,hsList,UserpersSearch.class)
  }

  def csiFindByLoansaldo(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, pers.shortname as pers_name, '' as dep_name, '' as group_name"
    hsSql.from='user left join pers on (user.pers_id=pers.id)'
    hsSql.where="user.usergroup_id!=1 and user.loansaldo!=0"
    hsSql.order="name asc"

    searchService.fetchData(hsSql,null,null,null,null,UserpersSearch.class)
  }

  def csiFindByPenalty(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, pers.shortname as pers_name, '' as dep_name, '' as group_name"
    hsSql.from='user left join pers on (user.pers_id=pers.id)'
    hsSql.where="user.usergroup_id!=1 and user.penalty!=0"
    hsSql.order="name asc"

    searchService.fetchData(hsSql,null,null,null,null,UserpersSearch.class)
  }

  def csiFindByDepartment(iDepId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong = [dep_id:iDepId]

    hsSql.select="*, pers.fullname as pers_name, department.name as dep_name, usergroup.name as group_name"
    hsSql.from='user join pers on (user.pers_id=pers.id) join department on (user.department_id=department.id) join usergroup on (user.usergroup_id=usergroup.id)'
    hsSql.where="(user.department_id=:dep_id or (department.parent=:dep_id and department.parent!=0)) and user.modstatus=1"
    hsSql.order="if(user.department_id=:dep_id,1,0) desc, user.department_id asc, pers.fullname"

    searchService.fetchData(hsSql,hsLong,null,null,null,UserpersSearch.class)
  }

  def csiFindByAccessrigth(sFieldname){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, pers.fullname as pers_name, department.name as dep_name, usergroup.name as group_name"
    hsSql.from='user join pers on (user.pers_id=pers.id) join department on (user.department_id=department.id) join usergroup on (user.usergroup_id=usergroup.id)'
    hsSql.where="user.modstatus=1 and user.usergroup_id!=1 and usergroup.$sFieldname=1"
    hsSql.order="pers.fullname asc"

    searchService.fetchData(hsSql,null,null,null,null,UserpersSearch.class)
  }

  def csiFindPersuser(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*, pers.shortname as pers_name, '' as dep_name, '' as group_name"
    hsSql.from='user left join pers on (user.pers_id=pers.id)'
    hsSql.where="user.usergroup_id!=1 and user.modstatus=1 and user.pers_id>0"
    hsSql.order="pers.shortname asc"

    searchService.fetchData(hsSql,null,null,null,null,UserpersSearch.class)
  }

}