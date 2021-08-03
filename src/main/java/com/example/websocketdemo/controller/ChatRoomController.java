package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.repository.ChatRoomRepository;
import com.example.websocketdemo.repository.MemoryChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/chatRoom")
    @ResponseBody
    public List<ChatRoom>  chatRooms(){
        
        List<ChatRoom> rooms = chatRoomRepository.findAll();
        for (ChatRoom room : rooms) {
            System.out.println(room.getRoomId()+" "+room.getName());
        }
        return rooms;
    }

    @PostMapping("/add")
    public String addChatRoom(String chatRoomName, RedirectAttributes redirectAttributes){


        ChatRoom saveRoom = chatRoomRepository.save(ChatRoom.create(chatRoomName));
        redirectAttributes.addAttribute("status", true);
        return "redirect:/chatRoom.html";
    }

    //테스트용 데이터 추가
    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init(){
        chatRoomRepository.save(ChatRoom.create("Room1"));
        chatRoomRepository.save(ChatRoom.create("Room2"));
        chatRoomRepository.save(ChatRoom.create("Room3"));

        chatRoomRepository.save(ChatRoom.create("Room4"));

    }

}
