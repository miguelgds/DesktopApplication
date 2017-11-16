# DesktopApplication

ESCRITORIOS:
-----------
* [X] Crear un escritorio
* [X] Van asociados a un usuario y tienen un nombre y una imagen de fondo. 
* [X] Son ordenables y eliminables mediante drag&drop.
* [X] Se le pueden añadir tantas aplicaciones/páginas como se quiera.
* [X] El propietario podrá compartir el escritorio, especificando una lista de grupos de usuarios y el permiso("Lectura" o "Lectura-Escritura") 
	* Si me han compartido un escritorio con permisos RW podré:
	* [X] Eliminar el escritorio
	* [X] Eliminar un item 
		* [X] Habrá que enviar un evento de esta acción (cuando se elimina un item de tipo página, hay que eliminar la página relacionada)
	* [X] Añadir un item (página/aplicación)
	* [X] Reordenar los elementos
* [X] Todos los usuarios tendrán, al menos, un escritorio donde se situarán inicialmente las páginas que tenga compartidas
	* [X] Este escritorio no se persistirá en BD, se calcula cada vez en función de los elementos compartidos.

ELEMENTO DE ESCRITORIO:
----------------------
* [X] Pertenece a uno y sólo un escritorio.
* [X] Se le puede configurar un nombre, un icono, un color y una página/aplicación.
* [X] Se pueden mover a otro escritorio existente del mismo usuario (siempre y cuando el escritorio de destino no sea uno readonly/fijo)
* [X] Se puede establecer como favorito, por lo que será lo que se muestre al iniciar la aplicación (sólo puede haber un elemento favorito por usuario).
* [X] Son ordenables y eliminables mediante drag&drop (excepto que pertenezcan a un escritorio readonly).
* [X] Cuando se elimina una aplicación, habrá que suscribirse al evento para proceder a eliminar todos los items que la tengan asignada
* [ ] Tipo PAGINA COMPARTIDA
	* [ ] PROPIETARIO
		* [ ] Verla en el escritorio de COMPARTIDAS
		* [ ] Icono flecha hacia arriba
		* [X] Eliminar el item
	* [ ] COMPARTIDA R		
		* [ ] Verla en el escritorio de COMPARTIDAS
		* [ ] Icono clip
	* [ ] COMPARTIDA RW
		* [ ] Verla en el escritorio de COMPARTIDAS
	    * [ ] Icono flecha hacia arriba	
		* [X] Eliminar el item

