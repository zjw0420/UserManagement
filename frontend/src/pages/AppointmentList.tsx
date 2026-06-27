import { useEffect, useState, useCallback } from 'react';
import { appointmentApi } from '../api/appointment';
import type { AppointmentVo } from '../types';
import ConfirmDialog from '../components/ConfirmDialog';

const statusMap: Record<number, string> = { 1: '待看房', 2: '已取消', 3: '已看房' };

export default function AppointmentList() {
  const [list, setList] = useState<AppointmentVo[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const pageSize = 10;

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [delId, setDelId] = useState<number | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await appointmentApi.page(pageNum, pageSize, { name: keyword || undefined });
      const data = res.data;
      setList(data?.records ?? []);
      setTotal(data?.total ?? 0);
    } catch { /* */ }
  }, [pageNum, keyword]);

  useEffect(() => { load(); }, [load]);

  const handleStatus = async (id: number, newStatus: number) => {
    await appointmentApi.updateStatus(id, newStatus);
    load();
  };

  const confirmDelete = (id: number) => { setDelId(id); setConfirmOpen(true); };
  const doDelete = async () => { if (delId) { await appointmentApi.delete(delId); load(); } setConfirmOpen(false); };

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      <h1 className="page-title">预约管理</h1>
      <div className="toolbar">
        <input className="search-input" placeholder="🔍 搜索预约人姓名..." value={keyword} onChange={e => { setKeyword(e.target.value); setPageNum(1); }} />
        <div style={{ flex: 1 }} />
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>预约人</th><th>手机号</th><th>公寓ID</th><th>预约时间</th><th>状态</th><th style={{ width: 200 }}>操作</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={7} className="empty-cell">暂无数据</td></tr> : list.map(a => (
            <tr key={a.id}>
              <td>{a.id}</td><td>{a.name}</td><td>{a.phone}</td><td>{a.apartmentId}</td>
              <td>{a.appointmentTime}</td>
              <td><span className={`tag ${a.appointmentStatus === 1 ? 'tag-warn' : a.appointmentStatus === 3 ? 'tag-active' : 'tag-inactive'}`}>{statusMap[a.appointmentStatus] ?? a.appointmentStatus}</span></td>
              <td className="action-cell">
                {a.appointmentStatus === 1 && (
                  <>
                    <button className="btn-action" onClick={() => handleStatus(a.id, 3)}>完成看房</button>
                    <button className="btn-action warn" onClick={() => handleStatus(a.id, 2)}>取消</button>
                  </>
                )}
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

      <ConfirmDialog open={confirmOpen} title="确认删除" message="确定要删除该预约吗？" onConfirm={doDelete} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}
