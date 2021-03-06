package com.boc.bocsoft.mobile.bocmobile.buss.wealthmanagement.repealorder.presenter;

import com.boc.bocsoft.mobile.bii.bus.global.service.GlobalService;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.model.PsnXpadAccountQuery.PsnXpadAccountQueryParams;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.model.PsnXpadAccountQuery.PsnXpadAccountQueryResult;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.model.PsnXpadAutComTradStatus.PsnXpadAutComTradStatusParams;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.model.PsnXpadAutComTradStatus.PsnXpadAutComTradStatusResult;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.model.PsnXpadRecentAccountQuery.PsnXpadRecentAccountQueryParams;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.model.PsnXpadRecentAccountQuery.PsnXpadRecentAccountQueryResult;
import com.boc.bocsoft.mobile.bii.bus.wealthmanagement.service.WealthManagementService;
import com.boc.bocsoft.mobile.bii.common.BiiException.BiiResultErrorException;
import com.boc.bocsoft.mobile.bocmobile.base.Exception.BIIBaseSubscriber;
import com.boc.bocsoft.mobile.bocmobile.buss.wealthmanagement.repealorder.model.XpadAutComTradStatusViewModel;
import com.boc.bocsoft.mobile.bocmobile.buss.wealthmanagement.repealorder.ui.TransComContact;
import com.boc.bocsoft.mobile.bocmobile.buss.wealthmanagement.transinquire.model.XpadAccountQueryViewModel;
import com.boc.bocsoft.mobile.bocmobile.buss.wealthmanagement.transinquire.model.XpadRecentAccountQueryViewModel;
import com.boc.bocsoft.mobile.common.utils.BeanConvertor.BeanConvertor;
import com.boc.bocsoft.mobile.framework.rx.SchedulersCompat;
import com.boc.bocsoft.mobile.framework.rx.lifecycle.RxLifecycleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Contact：中银理财-撤单-组合购买
 * Created by zhx on 2016/9/19
 */
public class TransComPresenter implements TransComContact.Presenter {
    private TransComContact.View mView;
    private RxLifecycleManager mRxLifecycleManager;

    private GlobalService globalService;
    private WealthManagementService wealthManagementService;

    public TransComPresenter(TransComContact.View view) {
        this.mView = view;
        mView.setPresenter(this);
        mRxLifecycleManager = new RxLifecycleManager();

        globalService = new GlobalService();
        wealthManagementService = new WealthManagementService();
    }

    @Override
    public void psnXpadAutComTradStatus(final XpadAutComTradStatusViewModel viewModel) {
//        ((BussFragment) mView).showLoadingDialog("请稍候...");

        PsnXpadAutComTradStatusParams params = new PsnXpadAutComTradStatusParams();
        params.setAccountKey(viewModel.getAccountKey());
        params.setStartDate(viewModel.getStartDate());
        params.setEndDate(viewModel.getEndDate());

        wealthManagementService.psnXpadAutComTradStatus(params)
                .compose(SchedulersCompat.<PsnXpadAutComTradStatusResult>applyIoSchedulers())
                .subscribe(new BIIBaseSubscriber<PsnXpadAutComTradStatusResult>() {
                    @Override
                    public void handleException(BiiResultErrorException biiResultErrorException) {
//                        ((BussFragment) mView).closeProgressDialog();
                        mView.psnXpadAutComTradStatusFail(biiResultErrorException);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(PsnXpadAutComTradStatusResult psnXpadAutComTradStatusResult) {
//                        ((BussFragment) mView).closeProgressDialog();
                        mView.psnXpadAutComTradStatusSuccess(generateXpadAutComTradStatusViewModel(viewModel, psnXpadAutComTradStatusResult));

                    }
                });
    }

    // 生成“委托组合交易状况查询”的ViewModel（用result对ViewModel进行填充）
    private XpadAutComTradStatusViewModel generateXpadAutComTradStatusViewModel(XpadAutComTradStatusViewModel viewModel, PsnXpadAutComTradStatusResult result) {
        List<PsnXpadAutComTradStatusResult.AutComTradEntity> autComTradList = result.getList();
        List<XpadAutComTradStatusViewModel.AutComTradEntity> viewList = new ArrayList<XpadAutComTradStatusViewModel.AutComTradEntity>();

        for (PsnXpadAutComTradStatusResult.AutComTradEntity autComTradEntity : autComTradList) {
            XpadAutComTradStatusViewModel.AutComTradEntity entity = new XpadAutComTradStatusViewModel.AutComTradEntity();

            BeanConvertor.toBean(autComTradEntity, entity);

            viewList.add(entity);
        }

        viewModel.setList(viewList);
        viewModel.setRecordNumber(result.getRecordNumber());

        return  viewModel;
    }

    @Override
    public void psnXpadAccountQuery(final XpadAccountQueryViewModel xpadAccountQueryViewModel) {
//        ((BussFragment) mView).showLoadingDialog("请稍候...");
        PsnXpadAccountQueryParams psnXpadAccountQueryParams = buildPsnXpadAccountQueryParams(xpadAccountQueryViewModel);

        wealthManagementService.psnXpadAccountQuery(psnXpadAccountQueryParams)
                .compose(SchedulersCompat.<PsnXpadAccountQueryResult>applyIoSchedulers())
                .subscribe(new BIIBaseSubscriber<PsnXpadAccountQueryResult>() {
                    @Override
                    public void handleException(BiiResultErrorException biiResultErrorException) {
//                        ((BussFragment) mView).closeProgressDialog();
                        //                        mView.psnXpadAccountQueryFail(biiResultErrorException);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(PsnXpadAccountQueryResult psnXpadAccountQueryResult) {
//                        ((BussFragment) mView).closeProgressDialog();
                        mView.psnXpadAccountQuerySuccess(generateXpadAccountQueryViewModel(xpadAccountQueryViewModel, psnXpadAccountQueryResult));


                    }
                });
    }

    // 最近操作账户查询
    @Override
    public void psnXpadRecentAccountQuery(final XpadRecentAccountQueryViewModel xpadRecentAccountQueryViewModel) {
//        ((BussFragment) mView).showLoadingDialog("请稍候...");

        PsnXpadRecentAccountQueryParams psnXpadRecentAccountQueryParams = new PsnXpadRecentAccountQueryParams();
        wealthManagementService.psnXpadRecentAccountQuery(psnXpadRecentAccountQueryParams)
                .compose(SchedulersCompat.<PsnXpadRecentAccountQueryResult>applyIoSchedulers())
                .subscribe(new BIIBaseSubscriber<PsnXpadRecentAccountQueryResult>() {
                    @Override
                    public void handleException(BiiResultErrorException biiResultErrorException) {
//                        ((BussFragment) mView).closeProgressDialog();

                        mView.psnXpadRecentAccountQueryFail(biiResultErrorException);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(PsnXpadRecentAccountQueryResult psnXpadAccountQueryResult) {
//                        ((BussFragment) mView).closeProgressDialog();

                        BeanConvertor.toBean(psnXpadAccountQueryResult, xpadRecentAccountQueryViewModel);

                        mView.psnXpadRecentAccountQuerySuccess(xpadRecentAccountQueryViewModel);
                    }
                });
    }

    // 生成"查询客户理财账户信息"的请求参数
    private PsnXpadAccountQueryParams buildPsnXpadAccountQueryParams(XpadAccountQueryViewModel xpadAccountQueryViewModel) {
        PsnXpadAccountQueryParams psnXpadAccountQueryParams = new PsnXpadAccountQueryParams();
        psnXpadAccountQueryParams.setXpadAccountSatus(xpadAccountQueryViewModel.getXpadAccountSatus());
        psnXpadAccountQueryParams.setQueryType(xpadAccountQueryViewModel.getQueryType());
        return psnXpadAccountQueryParams;
    }

    // 生成"查询客户理财账户信息"的ViewModel
    private XpadAccountQueryViewModel generateXpadAccountQueryViewModel(XpadAccountQueryViewModel xpadAccountQueryViewModel, PsnXpadAccountQueryResult psnXpadAccountQueryResult) {
        List<PsnXpadAccountQueryResult.XPadAccountEntity> list = psnXpadAccountQueryResult.getList();
        List<XpadAccountQueryViewModel.XPadAccountEntity> modelList = new ArrayList<XpadAccountQueryViewModel.XPadAccountEntity>();

        for (PsnXpadAccountQueryResult.XPadAccountEntity accountEntity : list) {
            XpadAccountQueryViewModel.XPadAccountEntity padAccountEntity = new XpadAccountQueryViewModel.XPadAccountEntity();
            BeanConvertor.toBean(accountEntity, padAccountEntity);

            modelList.add(padAccountEntity);
        }

        xpadAccountQueryViewModel.setList(modelList);
        return xpadAccountQueryViewModel;
    }

    @Override
    public void subscribe() {
        //TODO onResume时需要做的工作
    }

    @Override
    public void unsubscribe() {
        //TODO 防止外界已经销毁，而后台线程的任务还在执行
        mRxLifecycleManager.onDestroy();
    }
}
