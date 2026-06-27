import { useEffect, useState, useCallback } from 'react';
import { apartmentApi } from '../api/apartment';
import { regionApi, fileApi, type RegionVo } from '../api/region';
import { facilityApi, labelApi, type DictEntity } from '../api/dictionary';
import type { ApartmentItemVo, ApartmentSubmitVo } from '../types';
import { validate } from '../utils/validate';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm: ApartmentSubmitVo = {
  name: '', introduction: '', addressDetail: '', isRelease: 1,
  facilityInfoIds: [], labelIds: [], feeValueIds: [], graphVoList: [],
};
const aptRules = {
  name:          { label: '名称', required: true },
  addressDetail: { label: '详细地址', required: true },
} as const;

export default function ApartmentList() {
  const [list, setList] = useState<ApartmentItemVo[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const pageSize = 10;

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<ApartmentSubmitVo>(emptyForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [delId, setDelId] = useState<number | null>(null);

  const [provinces, setProvinces] = useState<RegionVo[]>([]);
  const [cities, setCities] = useState<RegionVo[]>([]);
  const [districts, setDistricts] = useState<RegionVo[]>([]);
  const [facilities, setFacilities] = useState<DictEntity[]>([]);
  const [labels, setLabels] = useState<DictEntity[]>([]);
  const [uploading, setUploading] = useState(false);

  const load = useCallback(async () => {
    try {
      const res = await apartmentApi.page(pageNum, pageSize, { name: keyword || undefined });
      const data = res.data;
      setList((data?.records ?? []).sort((a, b) => a.id - b.id));
      setTotal(data?.total ?? 0);
    } catch { /* */ }
  }, [pageNum, keyword]);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    regionApi.provinces().then(r => setProvinces(r.data ?? []));
    facilityApi.list().then(r => setFacilities(r.data as DictEntity[] ?? []));
    labelApi.list().then(r => setLabels(r.data as DictEntity[] ?? []));
  }, []);

  useEffect(() => {
    if (!form.provinceId) { setCities([]); setDistricts([]); return; }
    regionApi.cities(form.provinceId).then(r => setCities(r.data ?? []));
    setDistricts([]);
  }, [form.provinceId]);

  useEffect(() => {
    if (!form.cityId) { setDistricts([]); return; }
    regionApi.districts(form.cityId).then(r => setDistricts(r.data ?? []));
  }, [form.cityId]);

  const openCreate = () => {
    setEditingId(null); setForm(emptyForm); setErrors({});
    setCities([]); setDistricts([]);
    setModalOpen(true);
  };

  const openEdit = async (id: number) => {
    try {
      const res = await apartmentApi.getDetailById(id);
      const a = res.data!;
      setEditingId(id);
      setForm({
        name: a.name, introduction: a.introduction, addressDetail: a.addressDetail,
        provinceId: a.provinceId, cityId: a.cityId, districtId: a.districtId,
        phone: a.phone, isRelease: a.isRelease,
        facilityInfoIds: a.facilityInfoList?.map(f => f.id) ?? [],
        labelIds: a.labelInfoList?.map(l => l.id) ?? [],
        feeValueIds: a.feeValueList?.map(f => f.id) ?? [],
        graphVoList: a.graphVoList ?? [],
      });
      setErrors({});
      if (a.provinceId) regionApi.cities(a.provinceId).then(r => setCities(r.data ?? []));
      if (a.cityId) regionApi.districts(a.cityId).then(r => setDistricts(r.data ?? []));
      setModalOpen(true);
    } catch { /* */ }
  };

  const handleSubmit = async () => {
    const errs = validate(form, aptRules);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try { await apartmentApi.save(editingId ? { ...form, id: editingId } : form); setModalOpen(false); load(); }
    catch { /* */ } finally { setSubmitting(false); }
  };

  const confirmDelete = (id: number) => { setDelId(id); setConfirmOpen(true); };
  const doDelete = async () => { if (delId) { await apartmentApi.delete(delId); load(); } setConfirmOpen(false); };

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const res = await fileApi.upload(file);
      const url = res.data!;
      setForm({ ...form, graphVoList: [...(form.graphVoList ?? []), { name: file.name, url }] });
    } catch { /* */ } finally { setUploading(false); }
  };

  const toggleFacility = (id: number) => {
    const ids = form.facilityInfoIds ?? [];
    setForm({ ...form, facilityInfoIds: ids.includes(id) ? ids.filter(x => x !== id) : [...ids, id] });
  };
  const toggleLabel = (id: number) => {
    const ids = form.labelIds ?? [];
    setForm({ ...form, labelIds: ids.includes(id) ? ids.filter(x => x !== id) : [...ids, id] });
  };

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      <h1 className="page-title">公寓管理</h1>
      <div className="toolbar">
        <input className="search-input" placeholder="🔍 搜索公寓名称..." value={keyword} onChange={e => { setKeyword(e.target.value); setPageNum(1); }} />
        <button className="btn btn-primary" onClick={openCreate}>➕ 新增公寓</button>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>名称</th><th>简介</th><th>地址</th><th>省/市/区</th><th>最低租金</th><th>房间数</th><th>状态</th><th style={{ width: 140 }}>操作</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={9} className="empty-cell">暂无数据</td></tr> : list.map((a, i) => (
            <tr key={a.id}>
              <td>{i + 1}</td><td>{a.name}</td><td>{a.introduction?.slice(0, 20)}{(a.introduction?.length ?? 0) > 20 ? '...' : ''}</td>
              <td>{a.addressDetail}</td><td>{[a.provinceName, a.cityName, a.districtName].filter(Boolean).join('/') || '-'}</td>
              <td>{a.minRent ?? '-'}</td><td>{a.roomCount ?? '-'}</td>
              <td><span className={`tag ${a.isRelease === 1 ? 'tag-active' : 'tag-inactive'}`}>{a.isRelease === 1 ? '已发布' : '未发布'}</span></td>
              <td className="action-cell">
                <button className="btn-action" onClick={() => openEdit(a.id)}>编辑</button>
                <button className="btn-action danger" onClick={() => confirmDelete(a.id)}>删除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={pageNum <= 1} onClick={() => setPageNum(pageNum - 1)}>上一页</button>
          <span>{pageNum} / {totalPages}</span>
          <button disabled={pageNum >= totalPages} onClick={() => setPageNum(pageNum + 1)}>下一页</button>
        </div>
      )}

      <Modal open={modalOpen} title={editingId ? '编辑公寓' : '新增公寓'} onClose={() => setModalOpen(false)} width={680}>
        <div className="form-grid">
          <div className="form-group">
            <label>名称 <span className="required">*</span></label>
            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
            {errors.name && <span className="form-error">{errors.name}</span>}
          </div>
          <div className="form-group"><label>简介</label><input value={form.introduction} onChange={e => setForm({ ...form, introduction: e.target.value })} /></div>
          <div className="form-group"><label>联系电话</label><input value={form.phone ?? ''} onChange={e => setForm({ ...form, phone: e.target.value })} placeholder="例如 13800138000" /></div>

          <div className="form-group"><label>省份</label>
            <select value={form.provinceId ?? ''} onChange={e => {
              const id = Number(e.target.value) || undefined;
              setForm({ ...form, provinceId: id, cityId: undefined, districtId: undefined });
            }}><option value="">请选择</option>{provinces.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}</select>
          </div>
          <div className="form-group"><label>城市</label>
            <select value={form.cityId ?? ''} onChange={e => {
              const id = Number(e.target.value) || undefined;
              setForm({ ...form, cityId: id, districtId: undefined });
            }} disabled={!form.provinceId}><option value="">请选择</option>{cities.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select>
          </div>
          <div className="form-group"><label>区县</label>
            <select value={form.districtId ?? ''} onChange={e => setForm({ ...form, districtId: Number(e.target.value) || undefined })} disabled={!form.cityId}><option value="">请选择</option>{districts.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}</select>
          </div>

          <div className="form-group">
            <label>详细地址 <span className="required">*</span></label>
            <input value={form.addressDetail} onChange={e => setForm({ ...form, addressDetail: e.target.value })} required />
            {errors.addressDetail && <span className="form-error">{errors.addressDetail}</span>}
          </div>

          <div className="form-group"><label>状态</label><select value={form.isRelease} onChange={e => setForm({ ...form, isRelease: Number(e.target.value) })}><option value={1}>已发布</option><option value={0}>未发布</option></select></div>

          {facilities.length > 0 && (
            <div className="form-group" style={{ gridColumn: '1 / -1' }}>
              <label>配套设施</label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 4 }}>
                {facilities.map(f => (
                  <label key={f.id} style={{ cursor: 'pointer', fontSize: 13 }}>
                    <input type="checkbox" checked={(form.facilityInfoIds ?? []).includes(f.id!)} onChange={() => toggleFacility(f.id!)} />{' '}{f.name as string}
                  </label>
                ))}
              </div>
            </div>
          )}

          {labels.length > 0 && (
            <div className="form-group" style={{ gridColumn: '1 / -1' }}>
              <label>标签</label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 4 }}>
                {labels.map(l => (
                  <label key={l.id} style={{ cursor: 'pointer', fontSize: 13 }}>
                    <input type="checkbox" checked={(form.labelIds ?? []).includes(l.id!)} onChange={() => toggleLabel(l.id!)} />{' '}{l.name as string}
                  </label>
                ))}
              </div>
            </div>
          )}

          <div className="form-group" style={{ gridColumn: '1 / -1' }}>
            <label>图片</label>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center', marginTop: 4 }}>
              {(form.graphVoList ?? []).map((g, i) => (
                <div key={i} style={{ position: 'relative', width: 80, height: 60, border: '1px solid var(--gray-300)', borderRadius: 4, overflow: 'hidden' }}>
                  <img src={g.url} alt={g.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  <button onClick={() => setForm({ ...form, graphVoList: (form.graphVoList ?? []).filter((_, j) => j !== i) })}
                    style={{ position: 'absolute', top: 0, right: 0, background: 'rgba(255,0,0,0.7)', color: '#fff', border: 'none', cursor: 'pointer', fontSize: 12, width: 20, height: 20 }}>✕</button>
                </div>
              ))}
              <label className="btn btn-cancel" style={{ cursor: 'pointer', padding: '6px 12px', fontSize: 12 }}>
                {uploading ? '上传中...' : '📷 上传图片'}
                <input type="file" accept="image/*" onChange={handleUpload} style={{ display: 'none' }} />
              </label>
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>{submitting ? '保存中...' : '保存'}</button>
        </div>
      </Modal>

      <ConfirmDialog open={confirmOpen} title="确认删除" message="确定要删除该公寓吗？" onConfirm={doDelete} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}
