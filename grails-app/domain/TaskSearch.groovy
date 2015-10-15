class TaskSearch {
  def searchService
  static mapping = { version false }

//////Task///////////////
  Integer id
  Integer tasktype_id
  Integer link
  String description
  Date inputdate
  Integer taskstatus
  Date term
  Long initiator
  Long executor
  Long remapper
  Integer department_id
  Integer company_id
//////Users//////////////
  String i_name
  String e_name

  def csiSelectTask(lInitiator,lExecutor,iTaskstatus,iDepartment_id,lExecutorFilter,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*, exec.name as e_name, init.name as i_name"
    hsSql.from="task left join user exec on (exec.id=task.executor) left join user init on (init.id=task.initiator)"
    hsSql.where="1=1"+
      (lInitiator>0?' AND (task.initiator=:lInitiator OR task.remapper=:lInitiator)':'')+
      (lExecutor>0?' AND (task.executor=:lExecutor OR (task.department_id=:iDepartment_id AND task.executor=0))':'')+
      (iTaskstatus>-1?" AND taskstatus=:taskstatus":"")+
      (iDepartment_id>0?" AND task.department_id=:iDepartment_id":"")+
      (lExecutorFilter>0?" AND executor=:lExecutor":"")
    hsSql.order="taskstatus asc, term asc"

    if(lInitiator>0)
      hsLong['lInitiator'] = lInitiator
    if(lExecutor>0){
      hsLong['lExecutor'] = lExecutor
      hsLong['iDepartment_id'] = User.get(lExecutor)?.department_id?:-1
    }
    if(iTaskstatus>-1)
      hsLong['taskstatus'] = iTaskstatus
    if(iDepartment_id>0)
      hsLong['iDepartment_id'] = iDepartment_id
    if(lExecutorFilter>0)
      hsLong['lExecutor'] = lExecutorFilter

    searchService.fetchDataByPages(hsSql,null,hsLong,null,null,null,null,iMax,iOffset,'task.id',true,TaskSearch.class)
  }
}