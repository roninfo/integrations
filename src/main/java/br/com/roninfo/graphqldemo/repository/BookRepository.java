package br.com.roninfo.graphqldemo.repository;

import br.com.roninfo.graphqldemo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,String> {

}
