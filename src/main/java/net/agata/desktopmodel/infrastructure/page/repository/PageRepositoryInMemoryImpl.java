package net.agata.desktopmodel.infrastructure.page.repository;

import java.util.Collection;
import java.util.stream.Collectors;

import net.agata.desktopmodel.domain.page.repository.PageRepository;
import net.agata.desktopmodel.domain.page.valueobject.SharedPage;
import net.agata.desktopmodel.infrastructure.database.InMemoryDatabase;
import net.agata.desktopmodel.subdomain.user.UserID;

public class PageRepositoryInMemoryImpl implements PageRepository {
    
    @Override
    public Collection<SharedPage> findSharedPagesByUser(UserID userId) {
	return InMemoryDatabase.USER_GROUP_USER.stream()
		       	    .filter(t_ugu -> t_ugu._2.equals(userId))
		       	    .flatMap(t_ugu -> InMemoryDatabase.USER_GROUP_PAGE.stream()
		       		    			  .filter(t -> t._1.equals(t_ugu._1))
		       		    			  .flatMap(t_ugp -> InMemoryDatabase.USER_PAGE.stream()
		       		    				  				      .filter(t_up -> !t_up._2.equals(userId))
		       		    				  				      .filter(t_up -> t_up._1.equals(t_ugp._2))
		       		    			  					      .map(t_up -> new SharedPage(t_ugp._2, t_up._2, t_ugp._3))))
		       	    .collect(Collectors.toList());
    }

}
