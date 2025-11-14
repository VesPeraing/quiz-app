package com.example.quizapp.controller;

import com.example.quizapp.model.Question;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QuizController {

    private final List<Question> allQuestions = new ArrayList<>();
    private final int RANDOM_QUESTION_COUNT = 2;

    public QuizController() {
        allQuestions.add(new Question(1, "本群美化用的什么人模（全小写英文）", List.of("goose"), "fixed"));
        allQuestions.add(new Question(2, "该“沅芷的衣橱”美化作者在贴吧或dc的昵称是什么？（回答ID或昵称都对，只需答出1个即可）", List.of("Rita", "沅芷", "ina00932"), "fixed"));
        allQuestions.add(new Question(3, "社交栏里，神殿司祭的名字叫什么？", List.of("约旦"), "fixed"));
        allQuestions.add(new Question(4, "成人用品店店主名字叫什么？", List.of("西里斯"), "fixed"));
        allQuestions.add(new Question(5, "pc第一周需要交多少£给贝利（只需输入数字）", List.of("100"), "fixed"));
        allQuestions.add(new Question(6, "家政课会在哪天进行考试？", List.of("周四"), "fixed"));
        allQuestions.add(new Question(7, "黑狼会出没在哪里？", List.of("森林"), "fixed"));

        allQuestions.add(new Question(8, "游戏内第7周后，假如PC承担了罗宾的房租，那么PC共计需要交给贝利多少£？（只需输入数字）", List.of("4000"), "random"));
        allQuestions.add(new Question(9, "血柠只能在什么时候采摘？", List.of("血月"), "random"));
        allQuestions.add(new Question(10, "神殿谁会为你安装贞操带？", List.of("约旦"), "random"));
        allQuestions.add(new Question(11, "谁会开车接送PC上下学？", List.of("艾弗里"), "random"));
        allQuestions.add(new Question(12, "PC在开局的中午，在神殿中参加弥撒时遇到的第一个可攻略的角色是谁？", List.of("悉尼"), "random"));
        allQuestions.add(new Question(13, "幽灵蘑菇可以在哪位li的家中获取？", List.of("凯拉尔"), "random"));
    }

    @GetMapping("/quiz")
    public String showQuestion(HttpSession session, Model model) {
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");
        if (currentIndex == null) {
            System.out.println("首次访问，正在初始化 Session 数据...");
            initializeQuestions(session);
            session.setAttribute("currentIndex", 0);
            currentIndex = 0;
        }

        List<Question> questions = getQuestions(session);
        if (currentIndex >= questions.size()) {
            return "success";
        }

        Question currentQuestion = questions.get(currentIndex);
        model.addAttribute("question", currentQuestion);
        model.addAttribute("currentIndex", currentIndex);
        model.addAttribute("totalQuestions", questions.size());

        return "quiz";
    }

    @GetMapping("/restart")
    public String restartQuiz(HttpSession session) {
        session.invalidate();
        return "redirect:/quiz";
    }

    @PostMapping("/submit")
    public String submitAnswer(@RequestParam("answer") String answer,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Integer currentIndex = (Integer) session.getAttribute("currentIndex");
        if (currentIndex == null) {
            return "redirect:/quiz";
        }

        List<Question> questions = getQuestions(session);
        Question currentQuestion = questions.get(currentIndex);

        List<String> correctAnswers = currentQuestion.getCorrectAnswers();
        String normalizedUserAnswer = normalizeAnswer(answer);

        boolean isCorrect = correctAnswers.stream()
                .map(this::normalizeAnswer)
                .anyMatch(normalizedUserAnswer::equals);

        if (!isCorrect) {
            redirectAttributes.addFlashAttribute("userWrongAnswer", answer);
            return "redirect:/fail"; // 跳转到失败页
        } else {
            session.setAttribute("currentIndex", currentIndex + 1);
            return "redirect:/quiz";
        }
    }

    // 【最终确认】处理失败页面的路由
    @GetMapping("/fail")
    public String showFailPage() {
        return "fail";
    }

    private void initializeQuestions(HttpSession session) {
        List<Question> fixedQuestions = allQuestions.stream()
                .filter(q -> "fixed".equals(q.getType()))
                .collect(Collectors.toList());

        List<Question> randomPool = allQuestions.stream()
                .filter(q -> "random".equals(q.getType()))
                .collect(Collectors.toList());
        Collections.shuffle(randomPool);
        List<Question> selectedRandomQuestions = randomPool.stream()
                .limit(RANDOM_QUESTION_COUNT)
                .collect(Collectors.toList());

        List<Question> finalQuizQuestions = new ArrayList<>(fixedQuestions);
        finalQuizQuestions.addAll(selectedRandomQuestions);
        session.setAttribute("quizQuestions", finalQuizQuestions);

        System.out.println("Session 数据初始化完成！总题数: " + finalQuizQuestions.size());
    }

    @SuppressWarnings("unchecked")
    private List<Question> getQuestions(HttpSession session) {
        List<Question> questions = (List<Question>) session.getAttribute("quizQuestions");
        return questions != null ? questions : Collections.emptyList();
    }

    private String normalizeAnswer(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("\\s", "").toLowerCase();
    }
}
