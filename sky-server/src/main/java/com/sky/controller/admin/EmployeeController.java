package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.*;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        /**
         * 构建EmployeeLoginVO对象，用于封装员工登录后的信息。
         *
         * 该方法通过Builder模式创建EmployeeLoginVO实例，并设置以下属性：
         * - id: 员工唯一标识符，来源于employee对象的getId()方法。
         * - userName: 员工用户名，来源于employee对象的getUsername()方法。
         * - name: 员工姓名，来源于employee对象的getName()方法。
         * - token: 登录凭证，由外部传入。
         *
         * @param employee 员工实体对象，包含员工的基本信息。
         * @param token 登录生成的凭证字符串。
         * @return 返回构建完成的EmployeeLoginVO对象。
         */
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 员工管理: 分页查询
     */
    @GetMapping("/page")
    public Result<PageResult<Employee>> list(EmployeePageQueryDTO employeePageQueryDTO){

        log.info("分页查询员工数据: {}", employeePageQueryDTO);
        PageResult<Employee> pageResult = employeeService.list(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 新增员工
     */
    @PostMapping
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工信息: {}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }


}
