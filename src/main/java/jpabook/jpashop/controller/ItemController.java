package jpabook.jpashop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@GetMapping("/items/new")
	public String createForm(Model model) {
		model.addAttribute("form", new BookForm());
		return "items/createItemForm";
	}

	@PostMapping("/items/new")
	public String create(BookForm form) {
		Book book = new Book();
		book.setName(form.getName());
		book.setPrice(form.getPrice());
		book.setStockQuantity(form.getStockQuantity());
		book.setAuthor(form.getAuthor());
		book.setIsbn(form.getIsbn());

		itemService.saveItem(book);

		return "redirect:/items";
	}

	@GetMapping("/items")
	public String list(Model model) {
		List<Item> items = itemService.findItems();
		model.addAttribute("items", items);
		return "items/itemList";
	}

	@GetMapping("items/{itemId}/edit")
	public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
		Book item = (Book)itemService.findOne(itemId);

		BookForm form = new BookForm();
		form.setId(item.getId());
		form.setName(item.getName());
		form.setPrice(item.getPrice());
		form.setStockQuantity(item.getStockQuantity());
		form.setAuthor(item.getAuthor());
		form.setIsbn(item.getIsbn());

		model.addAttribute("form", form);

		return "items/updateItemForm";
	}

	@PostMapping("items/{itemId}/edit")
	public String updateItemForm(@PathVariable String itemId, @ModelAttribute("form") BookForm form) {

		//이렇게 Controller에서 엔티티를 생성하고 조작하지 말기 !!
		// Book book = new Book();
		// book.setId(form.getId());
		// book.setName(form.getName());
		// book.setPrice(form.getPrice());
		// book.setStockQuantity(form.getStockQuantity());
		// book.setAuthor(form.getAuthor());
		// book.setIsbn(form.getIsbn());
		//식별자가 DB에 있는 객체(준영속상태 객체)
		//JPA가 관리하지 않음 -> DB에 update가 안 일어남!!
		//1.변경 감지 기능(dirty checking) 사용(실무에서 사용하는 방법)
		//	영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
		//	트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)
		//	이 동작해서 데이터베이스에 UPDATE SQL 실행
		//2.병합(merge) 사용(실무에서 잘 안 씀)
		//	준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능

		itemService.updateItem(form.getId(), form.getName(), form.getPrice(), form.getStockQuantity());
		//파라미터가 많으면 DTO로 해결

		return "redirect:/items";
	}
}
