# DesktopApplication

ESCRITORIOS:
-----------
* [X] Van asociados a un usuario y tienen un nombre y una imagen de fondo. 
* [X] Son ordenables y eliminables mediante drag&drop.
* [ ] Todos los usuarios tendrán, al menos, un escritorio donde se situarán inicialmente las páginas/aplicaciones que tenga compartidas
	* [ ] Este escritorio no se persistirá en BD, se calcula cada vez en función de los elementos compartidos.
	* [ ] Este escritorio no se puede eliminar.
	* [ ] Si se recoloca una página/aplicación compartidas en otro escritorio, dejará de verse en el de compartidas.
	* [ ] Este escritorio debe ser de sólo lectura (permitiendo mover items a otros escritorios)
* [X] Se le pueden añadir tantas aplicaciones/páginas como se quiera.
* [ ] Se puede mover un item a otro escritorio existente (siempre y cuando el escritorio de destino no sea uno readonly/fijo)

ELEMENTO DE ESCRITORIO:
----------------------
* [ ] Pertenece a uno y sólo un escritorio.
* [ ] Se le puede configurar un nombre, un icono, un color y una página/aplicación.
* [ ] Son ordenables y eliminables mediante drag&drop (excepto que pertenezcan a un escritorio readonly).
* [ ] Se puede establecer como favorito, por lo que será lo que se muestre al iniciar la aplicación (sólo puede haber un elemento favorito por usuario).

