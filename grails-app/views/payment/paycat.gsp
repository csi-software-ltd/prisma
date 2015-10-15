<label for="paycat">Категория:</label>
<g:select name="paycat" value="${paycat.size()==1?paycat[0].id:0}" from="${paycat}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}" onchange="togglePaycat(this.value)"/>
<script type="text/javascript">
	togglePaycat(${paycat.size()==1?paycat[0].id:0})
</script>