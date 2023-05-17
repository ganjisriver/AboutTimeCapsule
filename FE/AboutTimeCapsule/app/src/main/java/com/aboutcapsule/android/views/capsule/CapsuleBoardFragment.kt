package com.aboutcapsule.android.views.capsule

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.aboutcapsule.android.R
import com.aboutcapsule.android.databinding.FragmentCapsuleGroupBinding
import com.aboutcapsule.android.factory.CapsuleViewModelFactory
import com.aboutcapsule.android.model.CapsuleViewModel
import com.aboutcapsule.android.repository.CapsuleRepo
import com.aboutcapsule.android.util.GlobalAplication
import com.aboutcapsule.android.util.SpinnerGroupAvailAdapter
import com.aboutcapsule.android.views.mainpage.MainPageMainFragment
import com.aboutcapsule.android.views.mainpage.MainPageVisitedCapsuleMapFragment
import java.util.Calendar

class CapsuleBoardFragment : Fragment() {

    companion object{
        lateinit var binding : FragmentCapsuleGroupBinding
        lateinit var navController: NavController

        private var flag = false // 달력 visible 체크용

        private lateinit var viewModel : CapsuleViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_capsule_group,container,false)

        val capsuleId=requireArguments().getInt("capsuleId")
        val lat = requireArguments().getDouble("lat")
        val lng = requireArguments().getDouble("lng")

        Log.d("캡슐보드","$capsuleId /$lat / $lng")

        getCalendarDate()

        // 맵에서 넘어온 데이터들 ( 다이얼로그 열기버튼 클릭 시 , )
        var capsuleId = requireArguments().getInt("capsuleId")
        var lat =requireArguments().getDouble("lat")
        var lng =requireArguments().getDouble("lng")
        Log.d("넘어옴" , "$capsuleId /$lat / $lng")

        redirectPage()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()

        callingApi()

        setSpinner()

        findGroupOrMe()
    }


    // 스피너 설정
    private fun setSpinner (){
        val repository = CapsuleRepo()
        val capsuleViewModelFactory = CapsuleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, capsuleViewModelFactory).get(CapsuleViewModel::class.java)
        var groupOrprivate = true // 그룹,개인 판별
        if(!groupOrprivate) { // 그룹 일 경우
            var list = mutableListOf<String>("전체공개", "그룹공개", "공개범위 ▼") // 스피너 목록 placeholder 가장 마지막으로
            var adapter = SpinnerGroupAvailAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                list
            ) // 목록 연결 ,simple요거는 안드로이드가 제공하는 거
            binding.spinnerOpenRange.adapter = adapter // 어댑터 연결
            binding.spinnerOpenRange.setSelection(2) // 스피너 최초로 볼 수 있는 값 ( placeholder) 가장 마지막 idx로 넣어주면 됨
            binding.spinnerOpenRange.dropDownVerticalOffset = dipToPixels(17f).toInt() // 드롭다운 내려오는 위치 ( 스피너 높이만큼 )
            binding.spinnerOpenRange.onItemSelectedListener = object : // 스피너 목록 클릭 시
                AdapterView.OnItemSelectedListener { override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    if (list.size==3 &&binding.spinnerOpenRange.getItemAtPosition(2).equals("공개범위 ▼")) { // 플레이스 홀더 역할 클릭 시
                        list.remove("공개범위 ▼") // placeholder 역할 제거해주기
                    } else { // 스피너 목록 클릭 시 ( 범위 설정 api )
                        var text = binding.spinnerOpenRange.getItemAtPosition(position)
                        Log.d("스피너", "$text")
                        viewModel.modifyCapsule(1, replaceRange(text.toString()))  //  "수정할 캡슐 번호 , 범위 "
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //  아무것도 선택 안했을 경우
                }
            }
        }else{ // 개인일 경우
            var list =
                mutableListOf<String>("전체공개", "친구만", "나만 보기" , "공개범위 ▼") // 스피너 목록 placeholder 가장 마지막으로
            var adapter = SpinnerGroupAvailAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                list
            ) // 목록 연결 ,simple요거는 안드로이드가 제공하는 거
            binding.spinnerOpenRange.adapter = adapter // 어댑터 연결
            binding.spinnerOpenRange.setSelection(3) // 스피너 최초로 볼 수 있는 값 ( placeholder) 가장 마지막 idx로 넣어주면 됨
            binding.spinnerOpenRange.dropDownVerticalOffset =
                dipToPixels(17f).toInt() // 드롭다운 내려오는 위치 ( 스피너 높이만큼 )
            binding.spinnerOpenRange.onItemSelectedListener = object : // 스피너 목록 클릭 시
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (list.size==4 && binding.spinnerOpenRange.getItemAtPosition(3).equals("공개범위 ▼")
                    ) { // 플레이스 홀더 역할 클릭 시
                        list.remove("공개범위 ▼") // placeholder 역할 제거해주기
                    } else { // 스피너 목록 클릭 시 ( 범위 설정 api )
                        var text = binding.spinnerOpenRange.getItemAtPosition(position)
                        Log.d("스피너", "$text")
                        viewModel.modifyCapsule(1, replaceRange(text.toString())) //  "수정할 캡슐 번호 , 범위 "
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //  아무것도 선택 안했을 경우
                }
            }
        }
    }
    // 공개범위 (서버로 보내줄 형태로 변환 )
    private fun replaceRange(range :String ): String{
        if(range.equals("전체공개")) return "ALL"
        else if(range.equals("친구만")) return "FREIND" // ?
        else if(range.equals("나만 보기")) return "PRIVATE"
        else if(range.equals("그룹공개")) return "GROUP"
        else return "null"
    }


    // 스피너 드롭다운 클릭 시 아래로 내려오는 크기
    private fun dipToPixels(dipValue : Float) : Float{
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    // 그룹인지, 개인인지 , 봉인 했는지 ,안했는지 체크 (UI 구성)
    private fun findGroupOrMe(){
        // 그룹 , 개인 일 경우
        val GorA_flag = " " // Group or alone
        val OorC_flag = " " // Open or close
        if(GorA_flag.equals(" ")){
            // 그룹이면서 아직 봉인 했는지 안 했는지 체크
            if(OorC_flag == " 봉인 일 경우 "){
                binding.groupSign.visibility=View.VISIBLE
                binding.privateSign.visibility=View.GONE
                binding.memberlistSign.visibility=View.VISIBLE
                binding.dateCommentlayout.visibility=View.VISIBLE
                binding.capsuleRegistBtn.visibility=View.VISIBLE
            }else{ // 그룹 캡슐 봉인일 지정 후
                binding.groupSign.visibility=View.VISIBLE
                binding.privateSign.visibility=View.GONE
                binding.memberlistSign.visibility=View.VISIBLE
                binding.dateCommentlayout.visibility=View.GONE
                binding.capsuleRegistBtn.visibility=View.GONE
            }
        }else { // 개인일 경우
            binding.groupSign.visibility=View.GONE
            binding.privateSign.visibility=View.VISIBLE
            binding.memberlistSign.visibility=View.GONE
            binding.dateCommentlayout.visibility=View.GONE
            binding.capsuleRegistBtn.visibility=View.GONE
        }
    }

    private fun redirectPage(){
        // 상단 툴바 알림페이지로 리다이렉트
        val notiBtn = activity?.findViewById<ImageView>(R.id.toolbar_bell)
        notiBtn?.setOnClickListener{
           navController.navigate(R.id.action_capsuleGroupFragment_to_notificationMainFragment)
        }

        // 멤버 목록 클릭 시 , 다이얼로그 보여주기
        binding.memberlistSign.setOnClickListener{
            val dialog = CustomDialogMemberList()
            GlobalAplication.preferences.setInt("memberlist_capsuleId",1); // 캡슐 아이디 넘겨주기
            dialog.show(parentFragmentManager, "customDialog")
        }

        // 하단 '+'버튼 클릭 시 (Floating Action Btn )
        binding.fabBtn.setOnClickListener{
            navController.navigate(R.id.action_capsuleGroupFragment_to_articleRegistFragment)
        }
    }

    // api 호출
    private fun callingApi(){

        // 삭제 버튼 클릭 시
        binding.deleteBtn.setOnClickListener{
            val repository = CapsuleRepo()
            val capsuleViewModelFactory = CapsuleViewModelFactory(repository)
            viewModel = ViewModelProvider(this, capsuleViewModelFactory).get(CapsuleViewModel::class.java)
            viewModel.removeCapsule(1) // 삭제후 리다이렉트 로직 작성 , "삭제할 캡슐 번호 "
        }
    }

    // 네비게이션 세팅
    private fun setNavigation(){
        val navHostFragment =requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        CapsuleRegistFragment.navController = navHostFragment.navController
    }

    private fun getCalendarDate(){

        binding.dateCommentlayout.setOnClickListener {
            flag = true

            if(!flag){
                binding.dateCommentlayout.visibility=View.VISIBLE
                binding.datepickedlayout.visibility=View.GONE
            }else{
                binding.dateCommentlayout.visibility = View.GONE
                binding.datepickedlayout.visibility=View.VISIBLE
            }

            val cal = Calendar.getInstance()

            val data = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                binding.openAvailDate.text = "${year}년 ${month+1}월 ${day}일"
            }

            val textColor= ContextCompat.getColor(requireContext(),R.color.datePickerColor)

            val datePickerDialog = DatePickerDialog(requireContext(),R.style.MyDatePicker ,data, cal.get(
                Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()

            val posBtn =datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            val negBtn =datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            posBtn.setText("확인")
            negBtn.setText("취소")
            posBtn.setTextColor(textColor)
            negBtn.setTextColor(textColor)
        }

        binding.datepickedlayout.setOnClickListener {
            flag = true

            val cal = Calendar.getInstance()

            val data = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                binding.openAvailDate.text = "${year}년 ${month+1}월 ${day}일"
            }

            val textColor= ContextCompat.getColor(requireContext(),R.color.datePickerColor)

            val datePickerDialog = DatePickerDialog(requireContext(),R.style.MyDatePicker ,data, cal.get(
                Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()

            val posBtn =datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            val negBtn =datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            posBtn.setText("확인")
            negBtn.setText("취소")
            posBtn.setTextColor(textColor)
            negBtn.setTextColor(textColor)
        }

    }


}
