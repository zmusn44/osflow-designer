package cn.linkey.flowdesign.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;

/**
 * 数据库链接池druid监控平台的配置
 * 访问路径 http://localhost:8080/druid/index.html
 */
@Configuration//在项目启动时可以初始化配置
public class DruidConfiguration {
    @Bean
    public ServletRegistrationBean staViewServlet(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),
                "/druid/*");
        //白名单为空允许任何ip访问
        servletRegistrationBean.addInitParameter("allow","*");
        //ip黑名单(存在共同时，deny优先于allow)：如果满足deny的即提示：Sorry you are not permitted...
        servletRegistrationBean.addInitParameter("deny","127.0.0.2");
        //登录查看信息的账号密码
        servletRegistrationBean.addInitParameter("loginUsername","druid");
        servletRegistrationBean.addInitParameter("loginPassword","druid");
        //是否能够重置数据
        servletRegistrationBean.addInitParameter("resetEnable","true");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean statFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpn,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    //配置数据库的基本连接信息
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")//在application.properties中读取配置信息注入到DruidDataSource里
    public DataSource dataSource(){
        DruidDataSource druidDataSource = DataSourceBuilder.create().type(DruidDataSource.class).build();
        druidDataSource.setInitialSize(3);//初始化物理连接的数量
        try {
            druidDataSource.addFilters("stat,wall");//stat是sql监控，wall是防火墙(如果不添加则监控无效)，不能添加log4j不然会出错
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }

}