package com.usermgr.admin.controller.apartment;

import com.usermgr.common.result.Result;
import com.usermgr.model.entity.CityInfo;
import com.usermgr.model.entity.DistrictInfo;
import com.usermgr.model.entity.ProvinceInfo;
import com.usermgr.service.business.CityInfoService;
import com.usermgr.service.business.DistrictInfoService;
import com.usermgr.service.business.ProvinceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "省市区管理")
@RestController
@RequestMapping("/admin/region")
public class RegionInfoController {

    @Autowired
    private ProvinceInfoService provinceInfoService;

    @Autowired
    private CityInfoService cityInfoService;

    @Autowired
    private DistrictInfoService districtInfoService;

    @Operation(summary = "查询所有省份")
    @GetMapping("/province")
    public Result<List<ProvinceInfo>> provinceList() {
        List<ProvinceInfo> list = provinceInfoService.list();
        return Result.ok(list);
    }

    @Operation(summary = "根据省份查询城市")
    @GetMapping("/city/{provinceId}")
    public Result<List<CityInfo>> cityList(@PathVariable Long provinceId) {
        List<CityInfo> list = cityInfoService.list(
                new LambdaQueryWrapper<CityInfo>()
                        .eq(CityInfo::getProvinceId, provinceId)
        );
        return Result.ok(list);
    }

    @Operation(summary = "根据城市查询区县")
    @GetMapping("/district/{cityId}")
    public Result<List<DistrictInfo>> districtList(@PathVariable Long cityId) {
        List<DistrictInfo> list = districtInfoService.list(
                new LambdaQueryWrapper<DistrictInfo>()
                        .eq(DistrictInfo::getCityId, cityId)
        );
        return Result.ok(list);
    }

}
