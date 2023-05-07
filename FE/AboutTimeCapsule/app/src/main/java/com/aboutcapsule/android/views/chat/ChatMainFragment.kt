package com.aboutcapsule.android.views.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aboutcapsule.android.R
import com.aboutcapsule.android.databinding.FragmentChatMainBinding

class ChatMainFragment : Fragment() {

    lateinit var navController: NavController
    lateinit var binding : FragmentChatMainBinding
    lateinit var chatMainAdapter : ChatMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat_main,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        setChatingView()

        redirectPage()
    }


    private fun setChatingView(){
        val chatMainDataList= getChatMaindatas()
        chatMainAdapter = ChatMainAdapter()
        chatMainAdapter.itemList = chatMainDataList
        binding.chatMainRecyclerView.adapter = chatMainAdapter
    }

    // 채팅 ( data )
    private fun getChatMaindatas(): MutableList<ChatMainData>{
        var itemList = mutableListOf<ChatMainData>()

        for( i in 1 .. 10){
            var userprofile = R.drawable.heartimg
            var nickname = " 유저 닉 네 임 ${i}"
            var content = " 채팅 내용 "
            var date = " ${i}일 전"
            var tmp = ChatMainData(userprofile,nickname,content,date)
            itemList.add(tmp)
        }
        return itemList
    }

    private fun redirectPage(){

        // 상단 툴바 알림페이지로 리다이렉트
        val notiBtn = activity?.findViewById<ImageView>(R.id.toolbar_bell)
        notiBtn?.setOnClickListener{
            navController.navigate(R.id.action_chatMainFragment_to_notificationMainFragment)
        }
    }

    // 네비게이션 세팅
    private fun setNavigation(){
        val navHostFragment =requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

}